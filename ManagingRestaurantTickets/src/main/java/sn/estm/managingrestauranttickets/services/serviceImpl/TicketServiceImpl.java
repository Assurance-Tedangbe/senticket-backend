/* TicketServiceImpl class implementing the TicketService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import sn.estm.managingrestauranttickets.config.TicketPricing;
import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitAccountRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitStudentDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitPorterDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.SenderDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.RecipientDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.OriginalSenderDTO;

import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.statisticsdto.TicketStatisticsDTO;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.entities.Ticket;

import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;

import sn.estm.managingrestauranttickets.enumerations.TransactionType;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.TicketMapper;

import sn.estm.managingrestauranttickets.mappers.TransactionHistoryMapper;
import sn.estm.managingrestauranttickets.repositories.*;

import sn.estm.managingrestauranttickets.services.serviceInterfaces.NotificationService;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransactionHistoryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final UserRepository userRepository;
    private final TransactionHistoryService transactionHistoryService;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistoryMapper transactionHistoryMapper;
    private final PasswordEncoder passwordEncoder;  // For password validation
    private final NotificationService notificationService;

    @Override
    public List<TicketDTO> readTickets() {
        List<Ticket> tickets = ticketRepository.findAll();

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    /// Integ Paydunya ----------------------------------------------------------------------
    /// executePurchase : crée les tickets au moment du paiement ----------------------------
    @Transactional
    public List<TicketDTO> executePurchase(Long userId, int countA, int countB) {

        log.info("ACHAT — userId: {}, typeA: {}, typeB: {}", userId, countA, countB);

        if (countA < 0 || countB < 0 || (countA + countB) == 0) {
            throw new IllegalArgumentException(
                    "La commande doit contenir au moins un ticket");
        }

        // 1. Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageFormat.format(
                                "Utilisateur non trouvé avec ID: {0}", userId)));

        // 2. Vérifier que l'utilisateur a le rôle ETUDIANT
        if (!user.getRole().getName().equals("ETUDIANT")) {
            throw new IllegalStateException(
                    "Seuls les ETUDIANT peuvent acheter des tickets");
        }

        // 3. créer les tickets et les associer à l'utilisateur
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < countA; i++) tickets.add(buildTicket(TicketType.A, user));
        for (int i = 0; i < countB; i++) tickets.add(buildTicket(TicketType.B, user));

        List<Ticket> saved = ticketRepository.saveAll(tickets);
        log.info("✅ {} tickets créés pour: {}", saved.size(), user.getUsername());

        // 4. RECORD PURCHASE TRANSACTION : Enregistrer dans l'historique des transactions
        transactionHistoryService.recordPurchase(user, saved, TicketPricing.total(countA, countB));


        // 5. Retourner les tickets achetés en DTO
        return saved.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    private Ticket buildTicket(TicketType type, User owner) {
        return Ticket.builder()
                .type(type)
                .price(TicketPricing.priceOf(type))
                .status(TicketStatus.BOOKED)
                .user(owner)
                .build();
    }

    /// ******** debitAccount service *********
    @Transactional
    @Override
    public void debitAccount(DebitAccountRequestDTO debitAccountRequestDTO) {
        log.info("Processing debit request: Porter {} debiting Student {} for tickets {}",
                debitAccountRequestDTO.getDebitPorterDTO().getPorterUsername(),
                debitAccountRequestDTO.getDebitStudentDTO().getUsername(),
                debitAccountRequestDTO.getTicketIds());

        // 1. Validate Input ---
        validateDebitRequest(debitAccountRequestDTO);

        // 2. Retrieve and Validate Users ---
        User porter = validatePorter(debitAccountRequestDTO.getDebitPorterDTO());
        User student = validateStudent(debitAccountRequestDTO.getDebitStudentDTO());

        // 3. Check Authorization ---
        checkAuthorization(porter);

        // 4. Retrieve and Validate Tickets ---
        List<Ticket> tickets = ticketRepository.findAllById(debitAccountRequestDTO.getTicketIds());
        validateTickets(tickets, debitAccountRequestDTO.getTicketIds().size(), student);

        // 5. Process Debit for All Tickets ---
        processTicketsDebit(tickets, porter, student);

        log.info("Successfully debited {} tickets for Student {} by Portier {}",
                tickets.size(), student.getUsername(), porter.getUsername());
    }

    private void validateDebitRequest(DebitAccountRequestDTO debitRequest) {
        if (debitRequest == null) {
            throw new IllegalArgumentException("Debit request cannot be null");
        }
        if (debitRequest.getDebitPorterDTO() == null) {
            throw new IllegalArgumentException("Porter information is required");
        }
        if (debitRequest.getDebitStudentDTO() == null) {
            throw new IllegalArgumentException("Student information is required");
        }
        if (debitRequest.getTicketIds() == null || debitRequest.getTicketIds().isEmpty()) {
            throw new IllegalArgumentException("At least one ticket ID must be provided");
        }

        if (debitRequest.getDebitPorterDTO().getPorterUsername()
                .equals(debitRequest.getDebitStudentDTO().getUsername())) {
            throw new IllegalArgumentException("Portier and Etudiant cannot be the same");
        }
    }

    private User validatePorter(DebitPorterDTO debitPorterDTO) {
        User porter = userRepository.findById(debitPorterDTO.getPorterId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Porter user not found with ID: " + debitPorterDTO.getPorterId()));

        // Validate username matches
        if (!porter.getUsername().equals(debitPorterDTO.getPorterUsername())) {
            throw new IllegalArgumentException("Porter username does not match the provided ID");
        }
        return porter;
    }

    private User validateStudent(DebitStudentDTO studentDTO) {
        User student = userRepository.findById(studentDTO.getDebitStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student user not found with ID: " + studentDTO.getDebitStudentId()));

        // Validate username matches
        if (!student.getUsername().equals(studentDTO.getUsername())) {
            throw new IllegalArgumentException("Student username does not match the provided ID");
        }

        // Validate role is ETUDIANT
        if (!student.getRole().getName().equals("ETUDIANT")) {
            throw new IllegalStateException("Target user must have ETUDIANT role");
        }
        return student;
    }

    private void checkAuthorization(User porter) {
        String roleName = porter.getRole().getName();

        if (!roleName.equals("PORTIER")
            //   && !roleName.equals("ADMIN")
        ) {
            throw new AccessDeniedException(
                    "Only PORTIER  role can perform debit operations. Current role: " + roleName);
        }
    }

    // ── validateTickets —─────────
    private void validateTickets(List<Ticket> tickets, int expectedCount, User student) {
        // Check if all tickets were found
        if (tickets.size() != expectedCount) {
            throw new ResourceNotFoundException(
                    "One or more specified tickets were not found. Expected: "
                            + expectedCount + ", Found: " + tickets.size());
        }

        // Validate each ticket
        for (Ticket ticket : tickets) {
            // Check ownership
            if (ticket.getUser() == null || !ticket.getUser().getId().equals(student.getId())) {
                throw new IllegalStateException(
                        "Ticket " + ticket.getId() + " does not belong to the specified student");
            }

            // Check eligibility for debit
            if (ticket.getStatus() != TicketStatus.BOOKED) {
                throw new IllegalStateException(MessageFormat.format(
                        "Ticket ID {0} is not eligible for debit. Current status: {1}",
                        ticket.getId(), ticket.getStatus()));
            }
        }
    }

    // ── processTicketsDebit ──────────────────────────────────────────
    private void processTicketsDebit(List<Ticket> tickets, User porter, User student) {

        for (Ticket ticket : tickets) {
            ticket.markAsUsed();
        }
        // Batch save all updated tickets
        ticketRepository.saveAll(tickets);

        log.debug("Batch updated {} tickets to USED status", tickets);

        log.info("RECORD DEBIT TRANSACTION:");

        // Après avoir validé et traité le débit, enregistrement dans l'historique unifié
        transactionHistoryService.recordDebit(porter, student, tickets);

        // ✅ Notifier l'étudiant du débit (ne fait jamais échouer le débit)
        if (student.getFcmToken() != null) {
            notificationService.notifyDebit(
                    student.getFcmToken(),
                    porter.getUsername(),
                    tickets.size(),
                    tickets.get(0).getType().name()
            );
        }
    }

    // ── getPurchasedTicketsByUser ───────────────────
    @Override
    public List<TicketDTO> getPurchasedTicketsByUser(Long userId, TicketType ticketType) {
        log.info("Récupération des tickets achetés pour l'utilisateur ID: {} avec type: {}", userId, ticketType);

        // 1. Vérifier que l'utilisateur existe ---
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé avec ID: " + userId));

        // 2. Vérifier que l'utilisateur a le rôle ETUDIANT ---
        if (!user.getRole().getName().equals("ETUDIANT")) {
            throw new IllegalStateException(
                    "Seuls les utilisateurs avec le rôle ETUDIANT peuvent avoir des tickets");
        }

        // 3. Récupérer les tickets selon les critères: user, ticketType & ticketStatus
        List<Ticket> tickets = ticketRepository.findByUserAndTypeAndStatus(
                user, ticketType, TicketStatus.BOOKED);

        log.info("Trouvé {} tickets achetés pour l'utilisateur ID: {} avec type: {}",
                tickets.size(), userId, ticketType);

        // 4. Convertir en DTO et retourner ---
        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }
    /// ******* End debitAccount service *********

    /// ******* transferTickets service *********
    @Transactional
    @Override
    public TransactionHistoryDTO transferTickets(TransferTicketsRequestDTO transferTicketsRequestDTO) {
        log.info("Processing ticket transfer: Sender {} transferring {} tickets of type {} to Recipient {}",
                transferTicketsRequestDTO.getSenderDTO().getSenderUsername(),
                transferTicketsRequestDTO.getNumberOfTicketsToTransfer(),
                transferTicketsRequestDTO.getTicketType(),
                transferTicketsRequestDTO.getRecipientDTO().getRecipientUsername());

        // 1. Validate Input
        validateTransferRequest(transferTicketsRequestDTO);

        // 2. Retrieve and Validate Users
        User sender = validateSender(transferTicketsRequestDTO.getSenderDTO());
        User recipient = validateRecipient(transferTicketsRequestDTO.getRecipientDTO());

        // 3. Check Sender Authorization
        checkSenderAuthorization(sender);

        // 4. Validate Password
        validatePassword(transferTicketsRequestDTO.getSenderDTO().getSenderPassword(), sender.getPassword());

        // 5. Retrieve and Validate Tickets for Transfer
        List<Ticket> ticketsToTransfer = findTicketsForTransfer(
                sender,
                transferTicketsRequestDTO.getTicketType(),
                transferTicketsRequestDTO.getNumberOfTicketsToTransfer()
        );

        // 6. Process Ticket Transfer and create history
        TransactionHistory history = processTicketTransfer(ticketsToTransfer, sender, recipient);

        log.info("Successfully transferred {} tickets of type {} from {} to {}",
                ticketsToTransfer.size(),
                transferTicketsRequestDTO.getTicketType(),
                sender.getUsername(),
                recipient.getUsername());

        // 7. Convert to DTO and return
        return transactionHistoryMapper.toDto(history);
    }

    private void validateTransferRequest(TransferTicketsRequestDTO transferRequest) {
        if (transferRequest == null) {
            throw new IllegalArgumentException("Transfer request cannot be null");
        }
        if (transferRequest.getSenderDTO() == null) {
            throw new IllegalArgumentException("Sender information is required");
        }
        if (transferRequest.getRecipientDTO() == null) {
            throw new IllegalArgumentException("Recipient information is required");
        }
        if (transferRequest.getTicketType() == null) {
            throw new IllegalArgumentException("Ticket type is required");
        }
        if (transferRequest.getNumberOfTicketsToTransfer() == null ||
                transferRequest.getNumberOfTicketsToTransfer() <= 0) {
            throw new IllegalArgumentException("Number of tickets to transfer must be at least 1");
        }

        // Check if sender is trying to transfer to himself
        if (transferRequest.getSenderDTO().getSenderUsername()
                .equals(transferRequest.getRecipientDTO().getRecipientUsername())) {
            throw new IllegalArgumentException("Cannot transfer tickets to yourself");
        }
    }

    private User validateSender(SenderDTO senderDTO) {
        User sender = userRepository.findById(senderDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sender user not found with ID: " + senderDTO.getSenderId()));

        // Validate username matches
        if (!sender.getUsername().equals(senderDTO.getSenderUsername())) {
            throw new IllegalArgumentException("Sender username does not match the provided ID");
        }

        // Validate role is ETUDIANT
        if (!sender.getRole().getName().equals("ETUDIANT")) {
            throw new IllegalStateException("Sender must have ETUDIANT role");
        }

        return sender;
    }

    private User validateRecipient(RecipientDTO recipientDTO) {
        User recipient = userRepository.findById(recipientDTO.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipient user not found with ID: " + recipientDTO.getRecipientId()));

        // Validate username matches
        if (!recipient.getUsername().equals(recipientDTO.getRecipientUsername())) {
            throw new IllegalArgumentException("Recipient username does not match the provided ID");
        }

        // Validate role is ETUDIANT
        if (!recipient.getRole().getName().equals("ETUDIANT")) {
            throw new IllegalStateException("Recipient must have ETUDIANT role");
        }

        return recipient;
    }

    private void checkSenderAuthorization(User sender) {
        String roleName = sender.getRole().getName();

        if (!roleName.equals("ETUDIANT")) {
            throw new AccessDeniedException(
                    "Only ETUDIANT role can perform ticket transfers. Current role: " + roleName);
        }
    }

    // Remplacer la méthode validatePassword()
    private void validatePassword(String providedPassword, String storedPasswordHash) {
        // ✅ BCrypt — compare le mot de passe en clair avec le hash stocké
        if (!passwordEncoder.matches(providedPassword, storedPasswordHash)) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    private List<Ticket> findTicketsForTransfer(User sender, TicketType ticketType, int numberOfTickets) {

        // Find sender's tickets that have BOOKED status, and match the ticket type
        List<Ticket> availableTickets = ticketRepository
                .findByUserAndTypeAndStatus(sender, ticketType, TicketStatus.BOOKED);

        if (availableTickets.size() < numberOfTickets) {
            throw new IllegalStateException(String.format(
                    "Sender does not have enough tickets of type %s to transfer. Available: %d, Requested: %d",
                    ticketType, availableTickets.size(), numberOfTickets));
        }

        // Select the first N tickets (you might want to use a specific selection strategy)
        // new ArrayList<>(...) évite de manipuler une vue subList pendant le saveAll.
        return new ArrayList<>(availableTickets.subList(0, numberOfTickets));
    }

    private TransactionHistory processTicketTransfer(List<Ticket> tickets, User sender, User recipient) {

        for (Ticket ticket : tickets) {
            // Update ticket ownership (Reassignment)
            ticket.setUser(recipient);
        }
        // Batch save all updated tickets
        ticketRepository.saveAll(tickets);

        log.debug("Transferred {} tickets to user {}", tickets.size(), recipient.getUsername());

        log.info("RECORD TRANSFER TRANSACTION:");

        // Après avoir validé et traité le transfert, enregistrement dans l'historique unifié
        TransactionHistory history = transactionHistoryService.recordTransfer(
                sender, recipient, tickets);

        // ✅ Notifier le destinataire du transfert reçu
        if (recipient.getFcmToken() != null) {
            notificationService.notifyTransferReceived(
                    recipient.getFcmToken(),
                    sender.getUsername(),
                    tickets.size(),
                    tickets.get(0).getType().name()
            );
        }

        return history;
    }
    /// ******** End transfert service *********

    /// ********* cancelTransferTickets service ***********
    @Transactional
    @Override
    public void cancelTransferTickets(CancelTransferTicketsRequestDTO cancelTransferTicketsRequestDTO) {

        log.info("Attempting to cancel transfer for transaction {}",
                cancelTransferTicketsRequestDTO.getCancelTransferDTO().getTransactionId());

        // 1. Validate input
        validateCancelRequest(cancelTransferTicketsRequestDTO);

        // 2. Extract DTOs
        CancelTransferDTO cancelTransferDTO = cancelTransferTicketsRequestDTO.getCancelTransferDTO();
        Long transactionId = cancelTransferDTO.getTransactionId();
        OriginalSenderDTO originalSenderDTO = cancelTransferDTO.getOriginalSenderDTO();
        RecipientDTO currentOwnerDTO = cancelTransferDTO.getCurrentOwnerDTO();

        // 3. Retrieve transaction history from unified table
        TransactionHistory transactionHistory = transactionHistoryRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with ID: " + transactionId));

        // 4. Validate that it's a TRANSFER transaction
        if (transactionHistory.getTransactionType() != TransactionType.TRANSFER) {
            throw new IllegalStateException(
                    String.format("Transaction ID %d is not a transfer transaction. Type: %s",
                            transactionId, transactionHistory.getTransactionType()));
        }

        // 5. Validate that the transfer is not already cancelled
        if (Boolean.TRUE.equals(transactionHistory.getTransferCanceled())) {
            throw new IllegalArgumentException("This transfer has already been cancelled");
        }

        // 6. Validate that the original sender matches the transaction
        User originalSenderFromHistory = transactionHistory.getSender();
        if (originalSenderFromHistory == null ||
                !originalSenderFromHistory.getId().equals(originalSenderDTO.getSenderId()) ||
                !originalSenderFromHistory.getUsername().equals(originalSenderDTO.getSenderUsername())) {
            throw new IllegalArgumentException("Original sender information does not match the transaction record");
        }

        // 7. Validate that the current owner matches the transaction recipient
        User recipientFromHistory = transactionHistory.getRecipient();
        if (recipientFromHistory == null ||
                !recipientFromHistory.getId().equals(currentOwnerDTO.getRecipientId()) ||
                !recipientFromHistory.getUsername().equals(currentOwnerDTO.getRecipientUsername())) {
            throw new IllegalArgumentException("Current owner information does not match the transaction record");
        }

        // 8. Extract ticket IDs from transaction and validate them
        List<Long> historyTicketIds = extractTicketIdsFromTransaction(transactionHistory);
        List<Long> requestTicketIds = cancelTransferTicketsRequestDTO.getTicketIdsToCancel();

        Collections.sort(historyTicketIds);
        Collections.sort(requestTicketIds);
        if (!historyTicketIds.equals(requestTicketIds)) {
            throw new IllegalArgumentException("The ticket IDs provided do not match those in the original transfer");
        }

        // 9. Fetch tickets
        List<Ticket> ticketsToReassign = ticketRepository.findAllById(historyTicketIds);
        if (ticketsToReassign.size() != historyTicketIds.size()) {
            throw new ResourceNotFoundException("One or more tickets not found");
        }

        // 10. Retrieve original sender user
        User originalSender = userRepository.findById(originalSenderDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Original sender user not found"));

        // 11. Reassign tickets back to original sender
        for (Ticket ticket : ticketsToReassign) {
            // Verify ticket ownership before reassigning
            if (ticket.getUser() == null || !ticket.getUser().getId().equals(recipientFromHistory.getId())) {
                throw new IllegalStateException(
                        String.format("Ticket %d is not owned by the expected current owner", ticket.getId()));
            }
            ticket.setUser(originalSender);
        }
        ticketRepository.saveAll(ticketsToReassign);

        // 12. Update transaction history as cancelled
        transactionHistory.setTransferCanceled(true);
        transactionHistoryRepository.save(transactionHistory);

        log.info("Successfully cancelled transfer transaction {} and returned {} tickets to {}",
                transactionId, ticketsToReassign.size(), originalSender.getUsername());
    }

    private void validateCancelRequest(CancelTransferTicketsRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Cancel request cannot be null");
        }
        if (request.getCancelTransferDTO() == null) {
            throw new IllegalArgumentException("CancelTransferDTO is required");
        }
        if (request.getCancelTransferDTO().getTransactionId() == null) {
            throw new IllegalArgumentException("Transaction ID is required");
        }
        if (request.getCancelTransferDTO().getOriginalSenderDTO() == null) {
            throw new IllegalArgumentException("Original sender information is required");
        }
        if (request.getCancelTransferDTO().getCurrentOwnerDTO() == null) {
            throw new IllegalArgumentException("Current owner information is required");
        }
        if (request.getTicketIdsToCancel() == null || request.getTicketIdsToCancel().isEmpty()) {
            throw new IllegalArgumentException("At least one ticket ID must be provided");
        }
    }

    private List<Long> extractTicketIdsFromTransaction(TransactionHistory transHis) {
        String ticketIdsStr = transHis.getTicketIds();
        if (ticketIdsStr == null || ticketIdsStr.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Remove brackets if stored as "[1,2,3]"
        ticketIdsStr = ticketIdsStr.replace("[", "")
                .replace("]", "").trim();
        if (ticketIdsStr.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(ticketIdsStr.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    /// ********* end of cancelTransferTickets service ***********

    /// ------— getTicketStatistics()---------------------------------------------------
    /**
     * Service pour récupérer les statistiques des tickets
     * Retourne toutes les statistiques demandées : par utilisateur et globales
     * @param userId (optionnel) - Si fourni, retourne les stats uniquement pour cet utilisateur
     * @return TicketStatisticsDTO contenant toutes les statistiques
     */
    @Override
    @Transactional(readOnly = true)
    public TicketStatisticsDTO getTicketStatistics(Long userId) {
        log.info("Fetching ticket statistics for userId: {}", userId);

        // 1. Étudiants concernés : un seul si userId fourni, sinon tous
        List<User> students;
        if (userId != null) {
            User u = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
            students = "ETUDIANT".equals(u.getRole().getName()) ? List.of(u) : List.of();
        } else {
            students = userRepository.findByRoleName("ETUDIANT");
        }
        log.debug("Found {} students", students.size());

        // 2. stats par utilisateur (requêtes COUNT) --------------------------------------
        // Map pour stocker les statistiques de chaque étudiant (clé = username)
        Map<String, TicketStatisticsDTO.UserTicketStats> userStatsMap = new LinkedHashMap<>();
        for (User student : students) {
            // 2.1 Récupérer les tickets achetés par l'utilisateur
            int purchased = (int) ticketRepository.countByUserAndStatus(student, TicketStatus.BOOKED);
            log.debug("Student {} has {} purchased tickets", student.getUsername(), purchased);
            // 2.2 Récupérer les tickets débités pour cet utilisateur
            int debited   = (int) ticketRepository.countByUserAndStatus(student, TicketStatus.USED);
            log.debug("Student {} has {} debited tickets", student.getUsername(), debited);

            // 2.3 stats pour cet utilisateur étudiant
            userStatsMap.put(student.getUsername(),
                    TicketStatisticsDTO.UserTicketStats.builder()
                            .userId(student.getId())
                            .username(student.getUsername())
                            .firstName(student.getFirstName())
                            .lastName(student.getLastName())
                            .purchasedTicketsCount(purchased) // Nombre de tickets achetés
                            .debitedTicketsCount(debited) // Nombre de tickets débités
                            .totalTicketsCount(purchased + debited) // Total = achetés + débités
                            .build());
        }

        // 3. Stats globales -------------------------------------------------
        // 3.1 Total des tickets achetés (tous utilisateurs confondus)
        long totalPurchased = ticketRepository.countByStatus(TicketStatus.BOOKED);
        log.debug("Total purchased tickets (all users): {}", totalPurchased);
        // 3.2 Total des tickets débités (tous utilisateurs confondus)
        long totalDebited   = ticketRepository.countByStatus(TicketStatus.USED);
        log.debug("Total debited tickets (all users): {}", totalDebited);

        TicketStatisticsDTO.GlobalTicketStats globalStats =
                TicketStatisticsDTO.GlobalTicketStats.builder()
                        .totalPurchasedTickets(totalPurchased) // Total achetés
                        .totalDebitedTickets(totalDebited)  // Total débités
                        .totalTicketsProcessed(totalPurchased + totalDebited) // Total = achetés + débités
                        .build();

        // 4. la réponse finale -----------------------------------------
        log.info("Statistics built: {} users, {} global tickets", userStatsMap.size(), totalPurchased + totalDebited);

        return TicketStatisticsDTO.builder()
                .userStats(userStatsMap)  // Stats par utilisateur
                .globalStats(globalStats) // Stats globales
                .build();
    }
}