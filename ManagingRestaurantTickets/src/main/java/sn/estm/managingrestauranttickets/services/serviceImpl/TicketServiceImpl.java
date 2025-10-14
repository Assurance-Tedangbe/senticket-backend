/* TicketServiceImpl class implementing the TicketService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.text.MessageFormat;
import java.util.List;
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
}
