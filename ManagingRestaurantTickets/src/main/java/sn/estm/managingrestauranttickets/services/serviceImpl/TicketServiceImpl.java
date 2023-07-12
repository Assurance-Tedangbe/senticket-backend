package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.repositories.TicketRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService{

    @Autowired
    TicketRepository ticketRepository;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public void createTicket(Ticket ticket) {
        ticketRepository.save(ticket);
        log.info("added object {}", ticket);
    }

    @Override
    public Ticket getTicketById(Long idTicket) {
        
        Optional<Ticket> optional = ticketRepository.findById(idTicket);
	    Ticket ticket = null;
		if(optional.isPresent())
		{
			ticket = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idTicket);
		}
		   return ticket;
    }

    @Override
    public void updateTicket(Long idTicket, Ticket newTicket) {
        Ticket ticket = this.getTicketById(idTicket);
        
        if(ticket==null) 
        throw new UnsupportedOperationException("Unimplemented method 'updateTicket'");
        
        else{
          ticket.setIdTicket(newTicket.getIdTicket());
          ticket.setCodePayement(newTicket.getCodePayement());
          ticket.setPrix(newTicket.getPrix());
          ticket.setReserve(newTicket.isReserve());
          ticket.setMenu(newTicket.getMenu());
          ticket.setEtudiant(newTicket.getEtudiant());
          ticketRepository.save(ticket);
          log.info("returned to postaman the update object {}", ticket);
        }
    }

    @Override
    public void deleteTicketById(Long idTicket) {
       ticketRepository.deleteById(idTicket);
    }
    
}
