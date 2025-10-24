/* TicketServiceImpl class implementing the TicketService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TicketCreationRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TicketFromDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TicketIdsToTransferDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferedTicketIdsToCancelDTO;
import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.TicketMapper;
import sn.estm.managingrestauranttickets.repositories.AccountRepository;
import sn.estm.managingrestauranttickets.repositories.TicketRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;


@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public List<TicketDTO> createTickets(TicketCreationRequestDTO ticketCreationRequestDTO) {

        log.info("Creating tickets with requests {}", ticketCreationRequestDTO);

        if (ticketCreationRequestDTO.getCountA() < 0 || ticketCreationRequestDTO.getCountB() < 0) {
            throw new IllegalArgumentException("Ticket counts cannot be negative");
        }

        List<Ticket> ticketsToSave = new ArrayList<>();
        LocalDateTime creationTime = LocalDateTime.now();

        // Create Type A tickets
        for (int i = 0; i < ticketCreationRequestDTO.getCountA(); i++) {
            Ticket ticketA = Ticket.builder()
                    .ticketType(TicketType.A)
                    .ticketPrice(100.0)
                    .ticketStatus(TicketStatus.AVAILABLE)
                    .booked(false)
                    .ticketCreationDate(creationTime)
                    .ticketDescription("Ticket Type A - " + (i + 1))
                    .build();
            ticketsToSave.add(ticketA);
        }

        // Create Type B tickets
        for (int i = 0; i < ticketCreationRequestDTO.getCountB(); i++) {
            Ticket ticketB = Ticket.builder()
                    .ticketType(TicketType.B)
                    .ticketPrice(150.0)
                    .ticketStatus(TicketStatus.AVAILABLE)
                    .booked(false)
                    .ticketCreationDate(creationTime)
                    .ticketDescription("Ticket Type B - " + (i + 1))
                    .build();
            ticketsToSave.add(ticketB);
        }

        // Use saveAll for efficient batch insertion
        List<Ticket> savedTickets = ticketRepository.saveAll(ticketsToSave);

        log.info("Successfully created tickets {} with requests {}",
                savedTickets.size(), ticketCreationRequestDTO);

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
        existingTicket.setAccount(ticketMapper.toEntity(ticketDTO).getAccount());
        existingTicket.setUser(ticketMapper.toEntity(ticketDTO).getUser());
        existingTicket.setMenu(ticketMapper.toEntity(ticketDTO).getMenu());

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


@Transactional
@Override
public List<TicketDTO> purchaseTickets(TicketFromDTO ticketFromDTO) {

    log.info("Deep: Purchasing tickets request: {}", ticketFromDTO);

    // Validate input
    if (ticketFromDTO == null || ticketFromDTO.getSelectedTicketIds() == null) {
        throw new IllegalArgumentException("Ticket purchase data must be provided");
    }

    Long accountId = ticketFromDTO.getAccountDTO().getAccountId();
    if (accountId == null) {
        throw new IllegalArgumentException("Account ID must be provided in TicketFromDTO.");
    } 

    List<Long> ticketIds = ticketFromDTO.getSelectedTicketIds();
    
    if (ticketIds.isEmpty()) {
        throw new IllegalArgumentException("No tickets provided for purchase");
    }

    // Get account and user
    Account account = accountRepository.findById(ticketFromDTO.getAccountDTO().getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Account not found with ID: {0}", 
                    ticketFromDTO.getAccountDTO().getAccountId())));

    User user = userRepository.findById(ticketFromDTO.getUserDTO().getUserId())
            .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}",
                     ticketFromDTO.getUserDTO().getUserId())));

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
        // Check eligibility por purchase: NOT booked AND TicketStatus.AVAILABLE
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
        /* if (ticket.getTicketPrice() == null) {
            ticket.setTicketPrice(ticket.getTicketType() == TicketType.A ? 100.0 : 150.0);
        }
       */

        totalPrice += ticket.getTicketPrice();
        availableTickets.add(ticket);

        //Count ticket types
        if(ticket.getTicketType() == TicketType.A){
            countAPurchased++;
        }else if(ticket.getTicketType() == TicketType.B){
            countBPurchased++;
        }
    }

    // Validate account balance
    Double balance = account.getBalance();
    if (balance == null || balance < totalPrice) {
        throw new IllegalStateException(MessageFormat.format(
                "Insufficient funds for totalPrice{} and balance {}", totalPrice, balance));
    }

    // Deduct total price from account
    account.setBalance(balance - totalPrice);
    Account savedAccount = accountRepository.save(account);

    // Update each purchase ticket
    LocalDateTime purchaseDateTime = LocalDateTime.now();
    List<Ticket> purchasedTickets;
    List<Ticket> inPurchasingTickets = new ArrayList<>();

    for (Ticket ticket : availableTickets) {
        ticket.setBooked(true);
        ticket.setTicketStatus(TicketStatus.BOOKED);
        ticket.setTicketPurchaseDate(purchaseDateTime);
        ticket.setPayementCode(UUID.randomUUID().toString());
        ticket.setAccount(savedAccount);
        ticket.setUser(user);
        
      //  Ticket savedTicket = ticketRepository.save(ticket);

        //collect purchased tickets
      //  purchasedTickets.add(savedTicket);
          inPurchasingTickets.add(ticket);

     /*   log.info("Ticket purchased successfully. ticketId={},+ ticketType={}, ticketPrice={}, paymentCode={}",
                  savedTicket.getTicketId(), savedTicket.getTicketType(),
                  savedTicket.getTicketPrice(), savedTicket.getPayementCode());*/
    }
    // Lines commented from 394 to 401 is to summarize to this one line
    purchasedTickets = ticketRepository.saveAll(inPurchasingTickets);

    log.info("Successfully purchased {} tickets for account {}. Total amount: {}. Type A: {}, Type B: {}",
            purchasedTickets.size(), savedAccount.getAccountId(), totalPrice, countAPurchased, countBPurchased);

    // Automatically recreate the purchased tickets to maintain inventory
    if (countAPurchased > 0 || countBPurchased > 0) {
        TicketCreationRequestDTO ticketCreationRequestDTO = TicketCreationRequestDTO.builder()
                .countA(countAPurchased)  // Recreate same number of Type A tickets
                .countB(countBPurchased)  // Recreate same number of Type B tickets
                .build();

        createTickets(ticketCreationRequestDTO);

        log.info("Automatically recreated {} Type A tickets and {} Type B tickets to maintain inventory",
                countAPurchased, countBPurchased);
    }

    // return purchased tickets as DTOs
    return purchasedTickets.stream()
            .map(ticketMapper::toDto)
            .collect(Collectors.toList());
   }

   @Transactional
   @Override
   public void transferTickets(TicketIdsToTransferDTO ticketIdToTransferDTO) {

        log.info("Gemi: Attempting to transfer tickets {} from Account {} to Account {}",
                ticketIdToTransferDTO.getSelectedTicketIdsToTransfer(),
                ticketIdToTransferDTO.getFromAccountId(),
                ticketIdToTransferDTO.getToAccountId());

        // --- 1. Input Validation ---
        if (ticketIdToTransferDTO.getSelectedTicketIdsToTransfer() == null ||
                ticketIdToTransferDTO.getSelectedTicketIdsToTransfer().isEmpty()) {
            throw new IllegalArgumentException("The list of ticket IDs to transfer cannot be empty.");
        }
        if (ticketIdToTransferDTO.getFromAccountId() == null ||
                ticketIdToTransferDTO.getToAccountId() == null) {
            throw new IllegalArgumentException("Both sender (from) and recipient (to) account IDs must be provided.");
        }
        if (ticketIdToTransferDTO.getFromAccountId().
                equals(ticketIdToTransferDTO.getToAccountId())) {
            throw new IllegalArgumentException("Cannot transfer tickets to the same account.");
        }

        // --- 2. Retrieve Accounts ---
        Account fromAccount = accountRepository.findById(ticketIdToTransferDTO.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Sender account not found with ID: {0}",
                        ticketIdToTransferDTO.getFromAccountId())));

        Account toAccount = accountRepository.findById(ticketIdToTransferDTO.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "Recipient account not found with ID: {0}",
                        ticketIdToTransferDTO.getToAccountId())));

        // --- 3. Fetch Tickets ---
        List<Ticket> ticketsToTransfer = ticketRepository.
                findAllById(ticketIdToTransferDTO.getSelectedTicketIdsToTransfer());

        if (ticketsToTransfer.size() != ticketIdToTransferDTO.getSelectedTicketIdsToTransfer().size()) {
            throw new ResourceNotFoundException("One or more tickets to transfer could not be found.");
        }

       // --- 4. Validation and Update ---
        List<Ticket> ticketsToSave = ticketsToTransfer.stream()
                .peek(ticket -> {
                    // Check transfer eligibility: booked=true AND status=BOOKED
                    if (!ticket.isBooked() || ticket.getTicketStatus() != TicketStatus.BOOKED) {
                        throw new IllegalStateException(MessageFormat.format(
                                "Ticket ID {0} is not eligible for transfer.Current Status: {1}, Booked: {2}",
                                ticket.getTicketId(), ticket.getTicketStatus(), ticket.isBooked()));
                    }

                    // Security Check: Ensure the ticket belongs to the sender
                    if (ticket.getAccount() == null || !ticket.getAccount().getAccountId().
                            equals(ticketIdToTransferDTO.getFromAccountId())) {
                        throw new IllegalStateException(MessageFormat.format(
                                "Ticket ID {} does not belong to the sender's account ID {}.",
                                ticket.getTicketId(), ticketIdToTransferDTO.getFromAccountId()));
                    }

                    // Update the ticket ownership (Reassignment)
                    // 1. Delete attachment from sender (by updating foreign key)
                    ticket.setAccount(toAccount);
                    // 2. Attach to the new user/owner
                    ticket.setUser(toAccount.getUser());
                })
                .collect(Collectors.toList());

        // --- 5. Batch Saving ---
        ticketRepository.saveAll(ticketsToSave);

        log.info("Successfully transferred {} tickets from account {} to account {}",
                ticketsToSave.size(), ticketIdToTransferDTO.getFromAccountId(),
                ticketIdToTransferDTO.getToAccountId());
    }

   @Transactional
   @Override
   public void cancelTransferTickets(TransferedTicketIdsToCancelDTO transferedTicketIdsToCancelDTO) {
       List<Long> ticketIdsToCancel = transferedTicketIdsToCancelDTO.getTicketIdsToCancel();
       Long originalSenderAccountId = transferedTicketIdsToCancelDTO.getOriginalSenderAccountId();
       Long currentOwnerAccountId = transferedTicketIdsToCancelDTO.getCurrentOwnerAccountId();

       log.info("Attempting to cancel transfer of tickets {} from current owner {} back to original sender {}",
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

       // --- 2. Retrieve Target Accounts and Target User ---

       // Target Account (Original Sender, where the tickets will return)
       Account targetAccount = accountRepository.findById(originalSenderAccountId)
               .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                       "Target account (original sender) not found with ID: {0}", originalSenderAccountId)));

       // Current Owner Account (The account currently holding the tickets)
       Account currentOwnerAccount = accountRepository.findById(currentOwnerAccountId)
               .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                       "Current owner account not found with ID: {0}", currentOwnerAccountId)));

       // --- 3. Fetch Tickets ---
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
                               "Ticket ID {0} is not in BOOKED status and cannot be re-transferred.",
                               ticket.getTicketId()));
                   }

                   // Security Check: Ensure the ticket is currently owned by the expected account (currentOwnerAccountId)
                   if (ticket.getAccount() == null || !ticket.getAccount().getAccountId().equals(currentOwnerAccountId)) {
                       throw new IllegalStateException(MessageFormat.format(
                               "Ticket ID {0} is currently owned by account ID {1}, not the expected current owner {2}.",
                               ticket.getTicketId(), ticket.getAccount() != null ? ticket.getAccount().getAccountId() : "N/A", currentOwnerAccountId));
                   }

                   // Update the ticket ownership to the original sender (cancellation/reassignment)
                   ticket.setAccount(targetAccount);
                   ticket.setUser(targetAccount.getUser());
               })
               .collect(Collectors.toList());

       // --- 5. Batch Save ---
       ticketRepository.saveAll(ticketsToSave);

       log.info("Successfully cancelled transfer for {} tickets. Reassigned from account {} back to account {}",
               ticketsToSave.size(), currentOwnerAccountId, originalSenderAccountId);
   }


   @Transactional
   @Override
   public void debitAccount(DebitRequestDTO debitRequestDTO) {
       log.info("Processing debit request: Portier {} debiting Etudiant {} for tickets {}",
               debitRequestDTO.getPortierAccountId(), debitRequestDTO.getEtudiantAccountId(),
               debitRequestDTO.getTicketIds());

       // --- 1. Validate Input ---
       validateDebitRequest(debitRequestDTO);

       // --- 2. Retrieve and Validate Accounts ---
       Account portierAccount = validatePortierAccount(debitRequestDTO.getPortierAccountId());
       Account etudiantAccount = validateEtudiantAccount(debitRequestDTO.getEtudiantAccountId());

       // --- 3. Retrieve and Validate Tickets ---
       List<Ticket> tickets = ticketRepository.findAllById(debitRequestDTO.getTicketIds());
       validateTickets(tickets, debitRequestDTO.getTicketIds().size(),
               debitRequestDTO.getEtudiantAccountId());

       // --- 4. Process Debit for All Tickets ---
       processTicketsDebit(tickets);

       log.info("Successfully debited {} tickets for Etudiant account {} by Portier {}",
               tickets.size(), debitRequestDTO.getEtudiantAccountId(),
               debitRequestDTO.getPortierAccountId());
   }

    private void validateDebitRequest(DebitRequestDTO debitRequest) {
        if (debitRequest == null) {
            throw new IllegalArgumentException("Debit request cannot be null");
        }
        if (debitRequest.getPortierAccountId() == null) {
            throw new IllegalArgumentException("Portier account ID is required");
        }
        if (debitRequest.getEtudiantAccountId() == null) {
            throw new IllegalArgumentException("Etudiant account ID is required");
        }
        if (debitRequest.getTicketIds() == null || debitRequest.getTicketIds().isEmpty()) {
            throw new IllegalArgumentException("At least one ticket ID must be provided");
        }
        if (debitRequest.getPortierAccountId().equals(debitRequest.getEtudiantAccountId())) {
            throw new IllegalArgumentException("Portier and Etudiant accounts cannot be the same");
        }
    }

    private Account validatePortierAccount(Long portierAccountId) {
        Account account = accountRepository.findById(portierAccountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portier account not found with ID: " + portierAccountId));

        User portierUser = account.getUser();
        if (portierUser == null || !portierUser.getRole().getName()
                .equalsIgnoreCase("PORTIER")) {
            throw new IllegalStateException("User must have PORTIER role to perform debit operations");
        }

        return account;
    }

    private Account validateEtudiantAccount(Long etudiantAccountId) {
        Account account = accountRepository.findById(etudiantAccountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Etudiant account not found with ID: " + etudiantAccountId));

        User etudiantUser = account.getUser();
        if (etudiantUser == null || etudiantUser.getRole().getName()
                .equalsIgnoreCase("ETUDIANT")) {
            throw new IllegalStateException("Target account must belong to an ETUDIANT");
        }

        return account;
    }

    private void validateTickets(List<Ticket> tickets, int expectedCount, Long etudiantAccountId) {
        // Check if all tickets were found
        if (tickets.size() != expectedCount) {
            throw new ResourceNotFoundException("One or more specified tickets were not found");
        }

        // Validate each ticket
        for (Ticket ticket : tickets) {
            // Check ownership
            if (ticket.getAccount() == null || !ticket.getAccount().getAccountId()
                    .equals(etudiantAccountId)) {
                throw new IllegalStateException(
                        "Ticket " + ticket.getTicketId() + " does not belong to the specified Etudiant account");
            }

            // Check eligibility for debit (must be booked with BOOKED status)
            if (!ticket.isBooked() || ticket.getTicketStatus() != TicketStatus.BOOKED) {
                throw new IllegalStateException(MessageFormat.format(
                        "Ticket ID {0} is not eligible for debit. Must be booked with BOOKED status. Current: {1}, Booked: {2}",
                        ticket.getTicketId(), ticket.getTicketStatus(), ticket.isBooked()));
            }
        }
    }

    private void processTicketsDebit(List<Ticket> tickets) {
        LocalDateTime usageTime = LocalDateTime.now();
        List<Ticket> updatedTickets = new ArrayList<>();

        for (Ticket ticket : tickets) {
            ticket.setTicketStatus(TicketStatus.USED);
            ticket.setTicketPurchaseDate(usageTime);
            updatedTickets.add(ticket);
        }
        // Batch save all updated tickets
        ticketRepository.saveAll(updatedTickets);

        log.debug("Batch updated {} tickets to USED status", updatedTickets);
    }
}
