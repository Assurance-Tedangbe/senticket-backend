/* TicketServiceImpl class implementing the TicketService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.*;
import sn.estm.managingrestauranttickets.dto.historydto.DebitHistoryDTO;
import sn.estm.managingrestauranttickets.dto.historydto.PurchaseHistoryDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransfertHistoryDTO;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.TicketMapper;
import sn.estm.managingrestauranttickets.mappers.UserMapper;
import sn.estm.managingrestauranttickets.repositories.TicketRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebitHistoryService;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PurchaseHistoryService;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransfertHistoryService;

import static java.util.regex.Pattern.matches;


@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TransfertHistoryService transfertHistoryService;
    private final DebitHistoryService debitHistoryService;
    private final PurchaseHistoryService purchaseHistoryService;
   // private final PasswordEncoder passwordEncoder; // For password validation

    @Transactional
    @Override
    public List<TicketDTO> createTickets(CreationTicketsRequestDTO creationTicketsRequestDTO) {

        //private static final double price_a = 100.0;

        log.info("Deep: Creating tickets with requests {}", creationTicketsRequestDTO);

        if (creationTicketsRequestDTO.getCountA() < 0 || creationTicketsRequestDTO.getCountB() < 0) {
            throw new IllegalArgumentException("Ticket counts cannot be negative");
        }

        List<Ticket> ticketsToSave = new ArrayList<>();
        LocalDateTime creationTime = LocalDateTime.now();

        // Create Type A tickets
       /* for (int i = 0; i < creationTicketsRequestDTO.getCountA(); i++) {
            Ticket ticketA = Ticket.builder()
                    .ticketType(TicketType.A)
                    .ticketPrice(100.0)
                    .payementCode("")
                    .ticketStatus(TicketStatus.AVAILABLE)
                    .booked(false)
                    .ticketCreationDate(creationTime)
                    .ticketDescription("Ticket Type A - " + (i + 1))
                    .build();
            ticketsToSave.add(ticketA);
        }*/

        // Create Type B tickets
        /*for (int i = 0; i < creationTicketsRequestDTO.getCountB(); i++) {
            Ticket ticketB = Ticket.builder()
                    .ticketType(TicketType.B)
                    .ticketPrice(150.0)
                    .payementCode("")
                    .ticketStatus(TicketStatus.AVAILABLE)
                    .booked(false)
                    .ticketCreationDate(creationTime)
                    .ticketDescription("Ticket Type B - " + (i + 1))
                    .build();
            ticketsToSave.add(ticketB);
        }*/

        // Use saveAll for efficient batch insertion
        List<Ticket> savedTickets = ticketRepository.saveAll(ticketsToSave);

       // creationTicketsRequestDTO.setTicketDTO(ticketMapper.toDtoSet(savedTickets));

      /*  log.info("Successfully created tickets {} with requests {}",
                savedTickets.size(), creationTicketsRequestDTO);*/

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
        if (ticket.isBooked() || ticket.getTicketStatus() != TicketStatus.AVAILABLE) {
            throw new IllegalStateException(MessageFormat.format(
                    "Ticket with ID: {0} is not available for purchase",
                    ticket.getTicketId()));
        }

        // Ensure ticket price is set based on type
        if (ticket.getTicketPrice() == null) {
            if (ticket.getTicketType() == TicketType.A) {
                ticket.setTicketPrice(100.0);
            } else if (ticket.getTicketType() == TicketType.B) {
                ticket.setTicketPrice(150.0);
            } else {
                throw new IllegalStateException(MessageFormat.format(
                        "Invalid ticket type for ticket ID: {0}", ticket.getTicketId()));
            }
        }

        totalPrice += ticket.getTicketPrice();
        availableTickets.add(ticket);

        //Count ticket types
        if (ticket.getTicketType() == TicketType.A) {
            countAPurchased++;
        } else if (ticket.getTicketType() == TicketType.B) {
            countBPurchased++;
        }
    }
   /* // Validate account balance
    Double balance = account.getBalance();
    if (balance == null || balance < totalPrice) {
        throw new IllegalStateException(MessageFormat.format(
                "Insufficient funds for totalPrice{} and balance {}", totalPrice, balance));
    }

    // Deduct total price from account
    account.setBalance(balance - totalPrice);
    Account savedAccount = accountRepository.save(account);*/

    // Update each purchase ticket
    List<Ticket> purchasedTickets;
    List<Ticket> inPurchasingTickets = new ArrayList<>();

    for (Ticket ticket : availableTickets) {
        ticket.setBooked(true);
        ticket.setTicketStatus(TicketStatus.BOOKED);
        ticket.setPayementCode(UUID.randomUUID().toString());
        ticket.setUser(user);

        //  Ticket savedTicket = ticketRepository.save(ticket);

        //collect purchased tickets
        inPurchasingTickets.add(ticket);

        PurchaseHistoryDTO purchaseHistoryDTO = PurchaseHistoryDTO.builder()
                .ticketDTO(ticketMapper.toDto(ticket))
                .purchaseUserDTO(userMapper.toDto(user))
                .build();
        purchaseHistoryService.createPurchaseHistory(purchaseHistoryDTO);
    }
    purchasedTickets = ticketRepository.saveAll(inPurchasingTickets);

    log.info("BEGIN BUILDING TICKETS:");
    if (countAPurchased > 0 || countBPurchased > 0) {
        CreationTicketsRequestDTO creationTicketsRequestDTO = CreationTicketsRequestDTO.builder()
                .countA(countAPurchased)  // Recreate same number of Type A tickets
                .countB(countBPurchased)  // Recreate same number of Type B tickets
                .build();

    log.info("Number of type A tickets {} and {} Type B tickets purchased {} ",
                countAPurchased, countBPurchased, purchaseTicketsRequestDTO.getSelectedTicketIds());

    List<Ticket> ticketsToSave = new ArrayList<>();
    LocalDateTime creationTime = LocalDateTime.now();

    // Create Type A tickets
    for (int i = 0; i < creationTicketsRequestDTO.getCountA(); i++) {
        Ticket ticketA = Ticket.builder()
                .ticketType(TicketType.A)
                .ticketPrice(100.0)
                .payementCode("")
                .ticketStatus(TicketStatus.AVAILABLE)
                .booked(false)
                .ticketCreationDate(creationTime)
                .ticketDescription("Ticket Type A - " + (i + 1))
                .user(user)
                .build();
        ticketsToSave.add(ticketA);
    }

    // Create Type B tickets
    for (int i = 0; i < creationTicketsRequestDTO.getCountB(); i++) {
        Ticket ticketB = Ticket.builder()
                .ticketType(TicketType.B)
                .ticketPrice(150.0)
                .payementCode("")
                .ticketStatus(TicketStatus.AVAILABLE)
                .booked(false)
                .ticketCreationDate(creationTime)
                .ticketDescription("Ticket Type B - " + (i + 1))
                .user(user)
                .build();
        ticketsToSave.add(ticketB);
    }

    // Use saveAll for efficient batch insertion
    List<Ticket> savedTickets = ticketRepository.saveAll(ticketsToSave);

    creationTicketsRequestDTO.setTicketDTO(ticketMapper.toDtoSet(savedTickets));

    log.info("END BUILDING TICKETS:");

    log.info("Creating tickets with details {}", creationTicketsRequestDTO);

    createTickets(creationTicketsRequestDTO);

    log.info("Successfully created tickets {} with requests {}",
            savedTickets.size(), creationTicketsRequestDTO);
}
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
            if (ticket.getUser() == null || !ticket.getUser().getUserId().equals(student.getUserId())) {
                throw new IllegalStateException(
                        "Ticket " + ticket.getTicketId() + " does not belong to the specified student");
            }

            // Check eligibility for debit (must be booked with BOOKED status)
            if (!ticket.isBooked() || ticket.getTicketStatus() != TicketStatus.BOOKED) {
                throw new IllegalStateException(MessageFormat.format(
                        "Ticket ID {0} is not eligible for debit. Must be booked with BOOKED status. Current: {1}, Booked: {2}",
                        ticket.getTicketId(), ticket.getTicketStatus(), ticket.isBooked()));
            }
        }
    }

    private void processTicketsDebit(List<Ticket> tickets, User porter,  User student) {
        List<Ticket> updatedTickets = new ArrayList<>();

        for (Ticket ticket : tickets) {
            ticket.setTicketStatus(TicketStatus.USED);
            updatedTickets.add(ticket);

            DebitHistoryDTO debitHistoryDTO= DebitHistoryDTO.builder()
                    .ticketDTO(ticketMapper.toDto(ticket))
                    .debitPorterDTO(userMapper.toDto(porter))
                    .debitStudentDTO(userMapper.toDto(student))
                    .build();
            debitHistoryService.createDebitHistory(debitHistoryDTO);
        }
        // Batch save all updated tickets
        ticketRepository.saveAll(updatedTickets);

        log.debug("Batch updated {} tickets to USED status", updatedTickets);
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
        List<Ticket> tickets = ticketRepository.findByUserAndTicketTypeAndBookedAndTicketStatus(
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
    public void transferTickets(TransferTicketsRequestDTO transferTicketsRequestDTO) {
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

        // 6. Process Ticket Transfer
        processTicketTransfer(ticketsToTransfer, sender, recipient);

        log.info("Successfully transferred {} tickets of type {} from {} to {}",
                ticketsToTransfer.size(),
                transferTicketsRequestDTO.getTicketType(),
                sender.getUsername(),
                recipient.getUsername());
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
        List<Ticket> availableTickets = ticketRepository.findByUserAndTicketTypeAndBookedAndTicketStatus(
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

    private void processTicketTransfer(List<Ticket> tickets, User sender, User recipient) {

        for (Ticket ticket : tickets) {
            // Update ticket ownership (Reassignment)
            ticket.setUser(recipient);

            TransfertHistoryDTO transfertHistoryDTO = TransfertHistoryDTO.builder()
                    .ticketDTO(ticketMapper.toDto(ticket))
                    .senderDTO(userMapper.toDto(sender))
                    .recipientDTO(userMapper.toDto(recipient))
                    .build();
            transfertHistoryService.createTransferHistory(transfertHistoryDTO);
        }

        // Batch save all updated tickets
        ticketRepository.saveAll(tickets);

        log.debug("Transferred {} tickets to user {}", tickets.size(), recipient.getUsername());
    }

 // ******** End transfert service *********

    // Suppl. methods
    @Override
    public TicketDTO readTicketById(Long ticketId) {

        log.info("Reading ticket by id: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketId)));

        return ticketMapper.toDto(ticket);
    }


    @Override
    public TicketDTO updateTicket(TicketDTO ticketDTO) {

        log.info("Updating ticket details: {}", ticketDTO);

        Ticket existingTicket = ticketRepository.findById(ticketDTO.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketDTO.getTicketId())));

        existingTicket.setTicketType(ticketDTO.getTicketType());
        existingTicket.setTicketPrice(ticketDTO.getTicketPrice());
        existingTicket.setBooked(ticketDTO.isBooked());
        existingTicket.setTicketStatus(ticketDTO.getTicketStatus());
        existingTicket.setTicketDescription(ticketDTO.getTicketDescription());
        //  existingTicket.setAccount(ticketMapper.toEntity(ticketDTO).getAccount());
        existingTicket.setUser(ticketMapper.toEntity(ticketDTO).getUser());
        // existingTicket.setMenu(ticketMapper.toEntity(ticketDTO).getMenu());

        Ticket updatedTicket = ticketRepository.save(existingTicket);

        log.info("Ticket updated successfully with ID: {}", updatedTicket.getTicketId());

        return ticketMapper.toDto(updatedTicket);
    }


    @Override
    public void deleteTicket(Long ticketId) {

        log.info("Deleting ticket with ID: {}", ticketId);

        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException(MessageFormat.format(
                    "Ticket not found with ID: {0}", ticketId));
        }

        ticketRepository.deleteById(ticketId);

        log.info("Deleted ticket with ID: {}", ticketId);
    }

    @Override
    public void updateTicketStatus(Long ticketId, TicketStatus newStatus) {

        log.info("Updating ticket status for ticket ID: {} to {}", ticketId, newStatus);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketId)));

        ticket.setTicketStatus(newStatus);

        ticketRepository.save(ticket);

        log.info("Updated ticket status for ticket ID: {} to {}", ticketId, newStatus);
    }


    @Override
    public void bookTicket(Long ticketId) {

        log.info("Booking ticket with ID: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketId)));

        ticket.setBooked(true);

        ticketRepository.save(ticket);

        log.info("Booked ticket with ID: {}", ticketId);
    }


    @Override
    public void unbookTicket(Long ticketId) {

        log.info("Unbooking ticket with ID: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Ticket not found with ID: {0}", ticketId)));

        ticket.setBooked(false);

        ticketRepository.save(ticket);

        log.info("Unbooked ticket with ID: {}", ticketId);
    }


    @Override
    public List<TicketDTO> readTicketsByStatus(TicketStatus ticketStatus) {
        log.info("Reading tickets with status: {}", ticketStatus);

        List<Ticket> tickets = ticketRepository.findByTicketStatus(ticketStatus);

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }


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

 /*  @Transactional
   @Override
   public void cancelTransferTickets(CancelTransferTicketsRequestDTO cancelTransferTicketsRequestDTO) {
       List<Long> ticketIdsToCancel = cancelTransferTicketsRequestDTO.getTicketIdsToCancel();
       Long originalSenderAccountId = cancelTransferTicketsRequestDTO.getOriginalSenderAccountId();
       Long currentOwnerAccountId = cancelTransferTicketsRequestDTO.getCurrentOwnerAccountId();

       log.info("Deep: Attempting to cancel transfer of tickets {} from current owner {} back to original sender {}",
               ticketIdsToCancel, currentOwnerAccountId, originalSenderAccountId);

       // --- 1. Input Validation ---
       if (ticketIdsToCancel == null || ticketIdsToCancel.isEmpty()) {
           throw new IllegalArgumentException("The list of ticket IDs to cancel cannot be empty.");
       }
       if (originalSenderAccountId == null || currentOwnerAccountId == null) {
           throw new IllegalArgumentException("Both original sender (from) and current owner (to) account IDs must be provided.");
       }
       if (originalSenderAccountId.equals(currentOwnerAccountId)) {
           throw new IllegalArgumentException("Invalid operation: Target and source accounts are the same.");
       }

       // --- 2. Retrieve accounts ---
       // Target Account (Original Sender, where the tickets will return)
       Account targetAccount = accountRepository.findById(originalSenderAccountId)
               .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                       "Target account (original sender) not found with ID: {0}", originalSenderAccountId)));

       // Current Owner Account (The account currently holding the tickets)
    *//*   Account currentOwnerAccount = accountRepository.findById(currentOwnerAccountId)
               .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                       "Current owner account not found with ID: {0}", currentOwnerAccountId)));*//*

       // --- 3. Fetch Tickets to cancel ---
       List<Ticket> ticketsToReassign = ticketRepository.findAllById(ticketIdsToCancel);

       if (ticketsToReassign.size() != ticketIdsToCancel.size()) {
           throw new ResourceNotFoundException("One or more tickets to cancel could not be found.");
       }

       // --- 4. Validation and Update (Reassignment) ---
       List<Ticket> ticketsToSave = ticketsToReassign.stream()
               .peek(ticket -> {
                   // Check transfer eligibility
                   if (!ticket.isBooked() || ticket.getTicketStatus() != TicketStatus.BOOKED) {
                       throw new IllegalStateException(MessageFormat.format(
                               "Ticket ID {0} is not eligible for transfer cancellation",
                               ticket.getTicketId()));
                   }

                   // Security Check: Ensure the ticket is currently owned by the expected account (currentOwnerAccountId)
                   if (ticket.getAccount() == null || !ticket.getAccount().getAccountId().equals(currentOwnerAccountId)) {
                       throw new IllegalStateException(MessageFormat.format(
                               "Ticket ID {0} is currently owned by account ID {1}, not the expected current owner {2}.",
                               ticket.getTicketId(), ticket.getAccount() != null ? ticket.getAccount().getAccountId() : "N/A", currentOwnerAccountId));
                   }

                   *//*if (ticket.getAccount() == null || !ticket.getAccount().getAccountId().equals(currentOwnerAccountId)) {
                       throw new IllegalStateException(MessageFormat.format(
                               "Ticket ID {0} is currently owned by account ID {1}, not the expected current owner {2}.",
                               ticket.getTicketId(), ticket.getAccount() != null ? ticket.getAccount().getAccountId() : "N/A", currentOwnerAccountId));
                   }*//*

                   // Update the ticket ownership to the original sender (cancellation/reassignment)
                 //  ticket.setAccount(targetAccount);
                   ticket.setUser(targetAccount.getUser());
               })
               .collect(Collectors.toList());

       // --- 5. Batch Save ---
       ticketRepository.saveAll(ticketsToSave);

       log.info("Successfully cancelled transfer for {} tickets. Reassigned from account {} back to account {}",
               ticketsToSave.size(), currentOwnerAccountId, originalSenderAccountId);
   }*/

    /* @Override
    public List<TicketDTO> readTicketsByAccountId(Long accountId) {

        log.info("Reading tickets for account with ID: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Account not found with ID: {0}", accountId)));

        List<Ticket> tickets = ticketRepository.findByAccount(account);

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

       @Override
    public List<TicketDTO> readTicketsByMenuIdAndUserId(Long menuId, Long userId) {

        log.info("Reading tickets for menu ID: {} and user ID: {}", menuId, userId);

        List<Ticket> tickets = ticketRepository.findByMenuMenuId(menuId).stream()
                .filter(ticket -> ticket.getAccount() != null && ticket.getAccount().getUser() != null
                        && ticket.getAccount().getUser().getUserId().equals(userId))
                .collect(Collectors.toList());

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

   @Override
    public List<TicketDTO> readTicketsByMenuIdAndUserIdAndStatus(Long menuId, Long userId,
                                                                 TicketStatus status) {
        log.info("Reading tickets for menu ID: {}, user ID: {}, and status: {}",
         menuId, userId, status);

        List<Ticket> tickets = ticketRepository.findByMenuMenuId(menuId).stream()
                .filter(ticket -> ticket.getAccount() != null && ticket.getAccount().getUser() != null
                        && ticket.getAccount().getUser().getUserId().equals(userId)
                        && ticket.getTicketStatus().equals(status))
                .collect(Collectors.toList());

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }
*/
}
