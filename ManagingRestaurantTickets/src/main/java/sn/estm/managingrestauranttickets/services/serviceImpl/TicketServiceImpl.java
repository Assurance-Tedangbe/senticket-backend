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
import sn.estm.managingrestauranttickets.dto.customisedto.TicketFromDTO;
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

    @Override
    public List<TicketDTO> createTickets(int countA, int countB) {

        log.info("Creating {} type A tickets and {} type B tickets", countA, countB);

        if (countA < 0 || countB < 0) {
            throw new IllegalArgumentException("Ticket counts cannot be negative");
        }

        List<Ticket> ticketsToSave = new ArrayList<>();
        LocalDateTime creationTime = LocalDateTime.now();

        // Create Type A tickets
        for (int i = 0; i < countA; i++) {
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
        for (int i = 0; i < countB; i++) {
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

        log.info("Successfully created {} tickets ({} type A, {} type B)",
                savedTickets.size(), countA, countB);

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

    log.info("Purchasing tickets request: {}", ticketFromDTO);

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

    // Validate tickets and calculate total price
    double totalPrice = 0.0;
    List<Ticket> availableTickets = new ArrayList<>();

    for (Ticket ticket : tickets) {
        // Check if ticket is available
        if (ticket.isBooked() || ticket.getTicketStatus() != TicketStatus.AVAILABLE) {
            throw new IllegalStateException(MessageFormat.format(
                    "Ticket with ID: {0} is not available for purchase",
                     ticket.getTicketId()));
        }

        // Ensure ticket price is set based on type
        if (ticket.getTicketPrice() == null) {
            ticket.setTicketPrice(ticket.getTicketType() == TicketType.A ? 100.0 : 150.0);
        }

        totalPrice += ticket.getTicketPrice();
        availableTickets.add(ticket);
    }

    // Validate account balance
    Double balance = account.getBalance();
    if (balance == null || balance < totalPrice) {
        throw new IllegalStateException(MessageFormat.format(
                "Insufficient funds", totalPrice, balance));
    }

    // Deduct total price from account
    account.setBalance(balance - totalPrice);
    Account savedAccount = accountRepository.save(account);

    // Update all tickets and collect purchased tickets
    LocalDateTime purchaseDateTime = LocalDateTime.now();
    List<Ticket> purchasedTickets = new ArrayList<>();

    
    for (Ticket ticket : availableTickets) {
        ticket.setBooked(true);
        ticket.setTicketStatus(TicketStatus.BOOKED);
        ticket.setTicketPurchaseDate(purchaseDateTime);
        ticket.setPayementCode(UUID.randomUUID().toString());
        ticket.setAccount(savedAccount);
        ticket.setUser(user);
        
        Ticket savedTicket = ticketRepository.save(ticket);
        purchasedTickets.add(savedTicket);

        log.info("Ticket purchased successfully. ticketId={},+ ticketType={}, ticketPrice={}, paymentCode={}",
                  savedTicket.getTicketId(), savedTicket.getTicketType(),
                  savedTicket.getTicketPrice(), savedTicket.getPayementCode());
    }

    log.info("Successfully purchased {} tickets for account {}. Total amount: {}",
            availableTickets.size(), savedAccount.getAccountId(), totalPrice);

    createTickets(5, 5);

    return purchasedTickets.stream()
            .map(ticketMapper::toDto)
            .collect(Collectors.toList());
   }


   @Override
   public void transferTickets(Long fromAccountId, Long toAccountId, Long ticketId) {
  
   }


   @Override
   public void cancelTransferTickets(Long fromAccountId, Long toAccountId, Long ticketId) {
   
   }


   @Override
   public void debitAccount(Long accountId, Long ticketId, Long debiterUserId) {
  
   }  
}
