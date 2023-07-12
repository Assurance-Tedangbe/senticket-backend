package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;

@Service
public class TicketServiceImpl implements TicketService{

    @Override
    public List<Ticket> getAllTickets() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllTickets'");
    }

    @Override
    public void createTicket(Ticket ticket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTicket'");
    }

    @Override
    public Ticket getTicketById(Long idTicket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTicketById'");
    }

    @Override
    public void updateTicket(Long idTicket, Ticket ticket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateTicket'");
    }

    @Override
    public void deleteTicketById(Long idTicket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteTicketById'");
    }
    
}
