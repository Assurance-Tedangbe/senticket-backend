package sn.estm.managingrestauranttickets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /** Tickets d'un utilisateur par type et statut */
    List<Ticket> findByUserAndTypeAndStatus(User user, TicketType type, TicketStatus status);

    /** Nombre de tickets d'un utilisateur par statut */
    long countByUserAndStatus(User user, TicketStatus status);
    /** Nombre total de tickets par statut */
    long countByStatus(TicketStatus status);
    boolean existsByUser(User user);
}
