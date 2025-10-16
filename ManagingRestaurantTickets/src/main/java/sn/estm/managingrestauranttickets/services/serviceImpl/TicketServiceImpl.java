/* TicketServiceImpl class implementing the TicketService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
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
    public TicketDTO createTicket(TicketDTO ticketDTO) {

        log.info("Creating ticket with details: {}", ticketDTO);
        
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        
        Ticket savedTicket = ticketRepository.save(ticket);

        log.info("Ticket created successfully with ID: {}", savedTicket.getTicketId());
       
        return ticketMapper.toDto(savedTicket);
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
        existingTicket.setPayementCode(ticketDTO.getPaymentCode());
        existingTicket.setBooked(ticketDTO.isBooked());
        existingTicket.setTicketStatus(ticketDTO.getTicketStatus());
        existingTicket.setTicketIssueDate(ticketDTO.getTicketIssueDate());
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


@Override
public void purchaseTicket(Long accountId, TicketDTO ticketDTO) {

    log.info("Purchasing ticket {} for account {}", ticketDTO, accountId);

    Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Account not found with ID: {0}", accountId)));

    if (ticketDTO == null || ticketDTO.getTicketId() == null) {
        throw new IllegalArgumentException("Ticket ID must be provided to purchase a ticket.");
    }

    Ticket ticket = ticketRepository.findById(ticketDTO.getTicketId())
            .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Ticket not found with ID: {0}", ticketDTO.getTicketId())));

    // Ensure ticket is available
    if (Boolean.TRUE.equals(ticket.isBooked())
            || (ticket.getTicketStatus() != null && ticket.getTicketStatus().name()
            .equalsIgnoreCase("BOOKED"))) {
        throw new IllegalStateException(MessageFormat.format("Ticket with ID: {0} is already booked.",
         ticket.getTicketId()));
    }

    // Determine price: use existing ticket price if set, otherwise default by type
    Double price = ticket.getTicketPrice();
    String typeStr = ticket.getTicketType() == null ? "" : ticket.getTicketType().toString();
    if (price == null) {
        if (typeStr.equalsIgnoreCase("blue")) {
            price = 100.0;
            ticket.setTicketPrice(price);
        } else { // default to green price if not blue
            price = 150.0;
            ticket.setTicketPrice(price);
        }
    }

    // Validate account balance (assumes Account has getBalance()/setBalance() returning Double)
    Double balance = account.getBalance();
    if (balance == null || balance < price) {
        throw new IllegalStateException("Insufficient funds on account to purchase the ticket.");
    }

    // Deduct price from account and save
    account.setBalance(balance - price);
    accountRepository.save(account);

    // Update ticket: mark as booked, set status, issue date and generate payment code, attach account
    ticket.setBooked(true);
    ticket.setTicketStatus(TicketStatus.BOOKED);
    ticket.setTicketIssueDate(LocalDateTime.now());
    ticket.setPayementCode(UUID.randomUUID().toString());
    ticket.setAccount(account);

    ticketRepository.save(ticket);

    log.info("Ticket purchased successfully. ticketId={}, accountId={}, amount={}, paymentCode={}",
            ticket.getTicketId(), accountId, price, ticket.getPayementCode());
}


   @Override
   public void transferTicket(Long fromAccountId, Long toAccountId, Long ticketId) {
  
   }


   @Override
   public void cancelTransferTicket(Long fromAccountId, Long toAccountId, Long ticketId) {
   
   }


   @Override
   public void debitAccount(Long accountId, Long ticketId, Long debiterUserId) {
  
   }  
}
