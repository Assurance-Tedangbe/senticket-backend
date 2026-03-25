/* TicketServiceImpl class implementing the TicketService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CreationTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitAccountRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.PurchaseTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitStudentDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitPorterDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.SenderDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.RecipientDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.OriginalSenderDTO;

import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.statisticsDTO.TicketStatisticsDTO;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.TransfertHistory;

import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;

import sn.estm.managingrestauranttickets.enumerations.TransactionType;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.TicketMapper;

import sn.estm.managingrestauranttickets.mappers.TransactionHistoryMapper;
import sn.estm.managingrestauranttickets.mappers.TransfertHistoryMapper;
import sn.estm.managingrestauranttickets.repositories.*;

import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransactionHistoryService;

import static java.util.regex.Pattern.matches;


@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final UserRepository userRepository;
    private final TransfertHistoryRepository transfertHistoryRepository;
    private final TransactionHistoryService transactionHistoryService;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistoryMapper transactionHistoryMapper;

    // private final PasswordEncoder passwordEncoder; // For password validation

    @Transactional
    @Override
    public List<TicketDTO> createTickets(CreationTicketsRequestDTO creationTicketsRequestDTO) {

        log.info(" Creating tickets with requests {}", creationTicketsRequestDTO);

        if (creationTicketsRequestDTO.getCountA() < 0 || creationTicketsRequestDTO.getCountB() < 0) {
            throw new IllegalArgumentException("Ticket counts cannot be negative");
        }

        List<Ticket> ticketsToSave = new ArrayList<>();

        // Use saveAll for efficient batch insertion
        List<Ticket> savedTickets = ticketRepository.saveAll(ticketsToSave);

        return savedTickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> readTickets() {
        List<Ticket> tickets = ticketRepository.findAll();

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<TicketDTO> purchaseTickets(PurchaseTicketsRequestDTO purchaseTicketsRequestDTO) {

        log.info("Deep: Purchasing tickets request: {}", purchaseTicketsRequestDTO);

        // Validate input
        if (purchaseTicketsRequestDTO == null || purchaseTicketsRequestDTO.getSelectedTicketIds() == null) {
            throw new IllegalArgumentException("Ticket purchase data must be provided");
        }

        List<Long> ticketIds = purchaseTicketsRequestDTO.getSelectedTicketIds();

        if (ticketIds.isEmpty()) {
            throw new IllegalArgumentException("No tickets provided for purchase");
        }

        // Get user
        User user = userRepository.findById(purchaseTicketsRequestDTO.getPurchaseUserDTO().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "User not found with ID: {0}",
                        purchaseTicketsRequestDTO.getPurchaseUserDTO().getUserId())));

        // Fetch all tickets
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        // Check if all tickets were found
        if (tickets.size() != ticketIds.size()) {
            throw new ResourceNotFoundException("One or more tickets not found");
        }

        // Validate tickets and calculate total price + count ticket types
        double totalPrice = 0.0;
        List<Ticket> availableTickets = new ArrayList<>();
        int countAPurchased = 0;
        int countBPurchased = 0;

        for (Ticket ticket : tickets) {
            // Check eligibility for purchase: NOT booked AND TicketStatus.AVAILABLE
            if (ticket.isBooked() || ticket.getStatus() != TicketStatus.AVAILABLE) {
                throw new IllegalStateException(MessageFormat.format(
                        "Ticket with ID: {0} is not available for purchase",
                        ticket.getId()));
            }

            // Ensure ticket price is set based on type
            if (ticket.getPrice() == null) {
                if (ticket.getType() == TicketType.A) {
                    ticket.setPrice(100.0);
                } else if (ticket.getType() == TicketType.B) {
                    ticket.setPrice(150.0);
                } else {
                    throw new IllegalStateException(MessageFormat.format(
                            "Invalid ticket type for ticket ID: {0}", ticket.getId()));
                }
            }

            totalPrice += ticket.getPrice();
            availableTickets.add(ticket);

            //Count ticket types
            if (ticket.getType() == TicketType.A) {
                countAPurchased++;
            } else if (ticket.getType() == TicketType.B) {
                countBPurchased++;
            }
        }

        // Update each purchase ticket
        List<Ticket> purchasedTickets;
        List<Ticket> inPurchasingTickets = new ArrayList<>();

        for (Ticket ticket : availableTickets) {
            ticket.setBooked(true);
            ticket.setStatus(TicketStatus.BOOKED);
            ticket.setUser(user);

            //collect purchased tickets
            inPurchasingTickets.add(ticket);
        }
        purchasedTickets = ticketRepository.saveAll(inPurchasingTickets);

        log.info("BEGIN BUILDING TICKETS:");
        if (countAPurchased > 0 || countBPurchased > 0) {
            CreationTicketsRequestDTO creationTicketsRequestDTO = CreationTicketsRequestDTO.builder()
                    .countA(countAPurchased)  // Recreate same number of Type A tickets
                    .countB(countBPurchased)
                    .build();

            log.info("Number of type A tickets {} and {} Type B tickets purchased {} ",
                    countAPurchased, countBPurchased, purchaseTicketsRequestDTO.getSelectedTicketIds());

            List<Ticket> ticketsToSave = new ArrayList<>();
            LocalDateTime creationTime = LocalDateTime.now();

            // Create Type A tickets
            for (int i = 0; i < creationTicketsRequestDTO.getCountA(); i++) {
                Ticket ticketA = Ticket.builder()
                        .type(TicketType.A)
                        .price(100.0)
                        .status(TicketStatus.AVAILABLE)
                        .booked(false)
                        .creationDate(creationTime)
                        .user(user)
                        .build();
                ticketsToSave.add(ticketA);
            }

            // Create Type B tickets
            for (int i = 0; i < creationTicketsRequestDTO.getCountB(); i++) {
                Ticket ticketB = Ticket.builder()
                        .type(TicketType.B)
                        .price(150.0)
                        .status(TicketStatus.AVAILABLE)
                        .booked(false)
                        .creationDate(creationTime)
                        .user(user)
                        .build();
                ticketsToSave.add(ticketB);
            }

            // Use saveAll for efficient batch insertion
            List<Ticket> savedTickets = ticketRepository.saveAll(ticketsToSave);

            creationTicketsRequestDTO.setTicketDTO(ticketMapper.toDtoSet(savedTickets));

            //createTickets(creationTicketsRequestDTO);

            log.info("Successfully created tickets {} with requests {}",
                    savedTickets.size(), creationTicketsRequestDTO);
        }
        log.info("RECORD PURCHASE TRANSACTION:");

        // Après avoir validé et traité l'achat, enregistrement dans l'historique unifié
        transactionHistoryService.recordPurchase(user, purchasedTickets);

        // return purchased tickets as DTOs
        return purchasedTickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    // ******** debitAccount service *********
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

            // Check eligibility for debit (must be booked with BOOKED status)
            if (!ticket.isBooked() || ticket.getStatus() != TicketStatus.BOOKED) {
                throw new IllegalStateException(MessageFormat.format(
                        "Ticket ID {0} is not eligible for debit. Must be booked with BOOKED status. Current: {1}, Booked: {2}",
                        ticket.getId(), ticket.getStatus(), ticket.isBooked()));
            }
        }
    }

    private void processTicketsDebit(List<Ticket> tickets, User porter, User student) {
        List<Ticket> updatedTickets = new ArrayList<>();

        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.USED);
            updatedTickets.add(ticket);
        }
        // Batch save all updated tickets
        ticketRepository.saveAll(updatedTickets);

        log.debug("Batch updated {} tickets to USED status", updatedTickets);

        log.info("RECORD DEBIT TRANSACTION:");

        // Après avoir validé et traité le débit, enregistrement dans l'historique unifié
        transactionHistoryService.recordDebit(porter, student, updatedTickets);
    }

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
                    "Seuls les utilisateurs avec le rôle ETUDIANT peuvent avoir des tickets achetés");
        }

        // 3. Récupérer les tickets selon les critères: user, ticketType, booked & ticketStatus
        List<Ticket> tickets = ticketRepository.findByUserAndTypeAndBookedAndStatus(
                user,
                ticketType,
                true,
                TicketStatus.BOOKED
        );

        log.info("Trouvé {} tickets achetés pour l'utilisateur ID: {} avec type: {}",
                tickets.size(), userId, ticketType);

        // 4. Convertir en DTO et retourner ---
        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }
    // ******* End debitAccount service *********

    // ******* transferTickets service *********
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

    private void validatePassword(String providedPassword, String storedPasswordHash) {
        if (!matches(providedPassword, storedPasswordHash)) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    private List<Ticket> findTicketsForTransfer(User sender, TicketType ticketType, int numberOfTickets) {

        // Find sender's tickets that are booked, have BOOKED status, and match the ticket type
        List<Ticket> availableTickets = ticketRepository.findByUserAndTypeAndBookedAndStatus(
                sender,
                ticketType,
                true,
                TicketStatus.BOOKED
        );

        if (availableTickets.size() < numberOfTickets) {
            throw new IllegalStateException(String.format(
                    "Sender does not have enough tickets of type %s to transfer. Available: %d, Requested: %d",
                    ticketType, availableTickets.size(), numberOfTickets));
        }

        // Select the first N tickets (you might want to use a specific selection strategy)
        return availableTickets.subList(0, numberOfTickets);
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
        return transactionHistoryService.recordTransfer(sender, recipient, tickets);
    }
    // ******** End transfert service *********

    // ********* cancelTransferTickets service ***********
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

        /* // 6. Check that the authenticated user is the original sender
        User authenticatedUser = getCurrentAuthenticatedUser(); // from security context
        if (!authenticatedUser.getUserId().equals(originalSenderDTO.getUserId())) {
        throw new AccessDeniedException("Only the original sender can cancel this transfer");
        }*/

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
   /*   private User getCurrentAuthenticatedUser() {
        // Implement based on your security context, e.g.:
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // return (User) authentication.getPrincipal();
        // For simplicity, assume a utility exists.
        return SecurityUtils.getCurrentUser();
    }*/

    //********* end of cancelTransferTickets service ***********

    // statistics
    @Override
    public TicketStatisticsDTO getTicketStatistics(Long userId) {
        log.info("Fetching ticket statistics for userId: {}", userId);

        // 1. Récupérer tous les utilisateurs ayant le rôle ETUDIANT
        List<User> students = userRepository.findByRoleName("ETUDIANT");

        // 2. Construire les statistiques par utilisateur
        Map<String, TicketStatisticsDTO.UserTicketStats> userStatsMap = new LinkedHashMap<>();

        for (User student : students) {
            // Tickets achetés par l'utilisateur (booked=true et status=BOOKED)
            List<Ticket> purchasedTickets = ticketRepository.findByUserAndBookedAndStatus(
                    student, true, TicketStatus.BOOKED);

            // Tickets débités pour cet utilisateur (status=USED)
            List<Ticket> debitedTickets = ticketRepository.findByUserAndStatus(
                    student, TicketStatus.USED);

            TicketStatisticsDTO.UserTicketStats stats = TicketStatisticsDTO.UserTicketStats.builder()
                    .userId(student.getId())
                    .username(student.getUsername())
                   // .firstName(student.getFirstName())
                   // .lastName(student.getLastName())
                    .purchasedTicketsCount(purchasedTickets.size())
                    .debitedTicketsCount(debitedTickets.size())
                    .totalTicketsCount(purchasedTickets.size() + debitedTickets.size())
                    .build();

            userStatsMap.put(student.getUsername(), stats);
        }

        // Si un userId spécifique est fourni, filtrer les statistiques
        if (userId != null) {
            User specificUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Ne garder que les statistiques de cet utilisateur
            Map<String, TicketStatisticsDTO.UserTicketStats> filteredStats = new LinkedHashMap<>();
            if (userStatsMap.containsKey(specificUser.getUsername())) {
                filteredStats.put(specificUser.getUsername(), userStatsMap.get(specificUser.getUsername()));
            }
            userStatsMap = filteredStats;
        }

        // 3. Statistiques globales
        // Total des tickets achetés (tous utilisateurs confondus)
        List<Ticket> allPurchasedTickets = ticketRepository.findByBookedAndStatus(true, TicketStatus.BOOKED);
        long totalPurchasedTickets = allPurchasedTickets.size();

        // Total des tickets débités (tous utilisateurs confondus)
        List<Ticket> allDebitedTickets = ticketRepository.findByStatus(TicketStatus.USED);
        long totalDebitedTickets = allDebitedTickets.size();

        TicketStatisticsDTO.GlobalTicketStats globalStats = TicketStatisticsDTO.GlobalTicketStats.builder()
                .totalPurchasedTickets(totalPurchasedTickets)
                .totalDebitedTickets(totalDebitedTickets)
                .totalTicketsProcessed(totalPurchasedTickets + totalDebitedTickets)
                .build();

        // 4. Statistiques des tickets disponibles (status = AVAILABLE)
        List<Ticket> availableTickets = ticketRepository.findByStatus(TicketStatus.AVAILABLE);

        long typeATicketsAvailable = availableTickets.stream()
                .filter(t -> t.getType() == TicketType.A)
                .count();

        long typeBTicketsAvailable = availableTickets.stream()
                .filter(t -> t.getType() == TicketType.B)
                .count();

        TicketStatisticsDTO.AvailableTicketsStats availableStats = TicketStatisticsDTO.AvailableTicketsStats.builder()
                .typeATicketsAvailable((int) typeATicketsAvailable)
                .typeBTicketsAvailable((int) typeBTicketsAvailable)
                .totalTicketsAvailable((int) availableTickets.size())
                .build();

        // 5. Construire la réponse finale
        return TicketStatisticsDTO.builder()
                .userStats(userStatsMap)
                .globalStats(globalStats)
                .availableStats(availableStats)
                .build();
    }

    // Suppl. methods
    @Override
    public List<TicketDTO> readTicketsByUserId(Long userId) {

        log.info("Reading tickets for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Account not found with ID: {0}", userId)));

        List<Ticket> tickets = ticketRepository.findByUser(user);

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDTO readTicketById(Long ticketId) {

        log.info("Reading ticket by id: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketId)));

        return ticketMapper.toDto(ticket);
    }
}
    /*
    @Override
    public void bookTicket(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketId)));
        ticket.setBooked(true);
        ticketRepository.save(ticket);
    }

    @Override
    public TicketDTO updateTicket(TicketDTO ticketDTO) {

        Ticket existingTicket = ticketRepository.findById(ticketDTO.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketDTO.getTicketId())));

        existingTicket.setType(ticketDTO.getTicketType());
        existingTicket.setPrice(ticketDTO.getTicketPrice());
        existingTicket.setBooked(ticketDTO.isBooked());
        existingTicket.setStatus(ticketDTO.getTicketStatus());
        existingTicket.setDescription(ticketDTO.getTicketDescription());
        existingTicket.setUser(ticketMapper.toEntity(ticketDTO).getUser());
        Ticket updatedTicket = ticketRepository.save(existingTicket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public void updateTicketStatus(Long ticketId, TicketStatus newStatus) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketId)));
        ticket.setStatus(newStatus);
        ticketRepository.save(ticket);
    }

    @Override
    public List<TicketDTO> readTicketsByStatus(TicketStatus ticketStatus) {

        List<Ticket> tickets = ticketRepository.findByStatus(ticketStatus);
        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }*/
