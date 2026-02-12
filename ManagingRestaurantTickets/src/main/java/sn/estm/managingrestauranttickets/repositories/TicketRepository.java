package sn.estm.managingrestauranttickets.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    /**
     * Retrieves a list of tickets associated with the specified user
     */
    List<Ticket> findByUser(User user);

    /**
     * Retrieves list of tickets by user, ticketType, booked, ticketStatus
     * @param user
     * @param ticketType
     * @param booked
     * @param ticketStatus
     * @return
     */
    List<Ticket> findByUserAndTicketTypeAndBookedAndTicketStatus(
            User user,
            TicketType ticketType,
            boolean booked,
            TicketStatus ticketStatus
    );

    /**
     * Retrieves a list of tickets that match the specified status.
     * @param ticketstatus the status of the tickets to retrieve
     */
    List<Ticket> findByTicketStatus(TicketStatus ticketstatus);

    /**
     * Retrieves a list of tickets based on their booked status.
     * @param booked the booked status to filter tickets by
     */
    List<Ticket> findByBooked(boolean booked);

    /**
     * Checks if a ticket with the specified status exists in the repository.
     * @param ticketStatus the status to check for existence
     * @return {@code true} if a ticket with the given status exists, {@code false} otherwise
     */
    boolean existsByTicketStatus(TicketStatus ticketStatus);

    /**
     * @param ticketId the unique identifier of the ticket to find
     * @return an Optional containing the Ticket entity with the specified ticket ID if found, or an empty Optional if not found
     */ 
    Optional<Ticket> findByTicketId(Long ticketId);

    /**
     * Retrieves a list of tickets associated with a specific user's ID.
     * @param userId the unique identifier of the user whose tickets are to be retrieved
     */
    List<Ticket> findByUserUserId(Long userId);

    /**
     * @param userId the unique identifier of the user whose tickets are to be retrieved
     * @param ticketStatus the status of the tickets to filter by (e.g., "ACTIVE", "USED", etc.)
     * @return a list of tickets objects matching the given user ID and status
     */
    List<Ticket> findByUserUserIdAndTicketStatus(Long userId, TicketStatus ticketStatus);
    
}
