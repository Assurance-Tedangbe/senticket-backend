package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
}
