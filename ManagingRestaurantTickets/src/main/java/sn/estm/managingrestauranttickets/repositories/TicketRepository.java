package sn.estm.managingrestauranttickets.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
   
    /**
     * Retrieves a list of tickets associated with the specified user 
     */
    List<Ticket> findByUser(User user);

    /**
     * Retrieves a list of tickets that match the specified status.
     * @param ticketstatus the status of the tickets to retrieve
     * @return a list of {@link Ticket} objects with the given status
     */
    List<Ticket> findByTicketStatus(TicketStatus ticketstatus);

    /**
     * Retrieves a list of tickets associated with the specified account.
     * @param account the account for which to find tickets
     * @return a list of tickets belonging to the given account
     */
  //  List<Ticket> findByAccount(Account account);

    /**
     * Retrieves a list of tickets based on their booked status.
     * @param booked the booked status to filter tickets by
     * @return a list of tickets with the specified booked status
     */
    List<Ticket> findByBooked(boolean booked);

    /**
     * Checks if a ticket with the specified status exists in the repository.
     * @param ticketStatus the status to check for existence
     * @return {@code true} if a ticket with the given status exists, {@code false} otherwise
     */
    boolean existsByTicketStatus(TicketStatus ticketStatus);

    /**
     * Retrieves an Optional containing the Ticket entity with the specified ticket ID.
     * @param ticketId the unique identifier of the ticket to find
     * @return an Optional containing the Ticket if found, or an empty Optional if not found
     */ 
    Optional<Ticket> findByTicketId(Long ticketId);

    /**
     * Retrieves a list of tickets associated with a specific user's ID.
     * @param userId the unique identifier of the user whose tickets are to be retrieved
     * @return a list of {@link Ticket} entities belonging to the specified user
     */
    List<Ticket> findByUserUserId(Long userId);


    /**
     * Retrieves a list of tickets associated with a specific user and having the specified status.
     *
     * @param userId the unique identifier of the user whose tickets are to be retrieved
     * @param ticketStatus the status of the tickets to filter by (e.g., "ACTIVE", "USED", etc.)
     * @return a list of {@link Ticket} objects matching the given user ID and status
     */
    List<Ticket> findByUserUserIdAndTicketStatus(Long userId, TicketStatus ticketStatus);

    /**
     * Retrieves a list of Ticket entities associated with the specified menu ID.
     * @param menuId the ID of the menu to filter tickets by
     * @return a list of Ticket entities linked to the given menu ID
     */
   // List<Ticket> findByMenuMenuId(Long menuId);

    /**
     * Retrieves a list of {@link Ticket} entities filtered by the specified menu ID and user ID.
     * @param menuId the ID of the menu to filter tickets by
     * @param userId the ID of the user to filter tickets by
     * @return a list of tickets associated with the given menu and user
     */
 //   List<Ticket> findByMenuMenuIdAndUserUserId(Long menuId, Long userId);

    /**
     * Retrieves a list of Ticket entities filtered by the specified menu ID, user ID, and status.
     * @param menuId the ID of the menu associated with the tickets
     * @param userId the ID of the user associated with the tickets
     * @param ticketStaus the status of the tickets to filter by
     * @return a list of Ticket entities matching the given menu ID, user ID, and status
     */
  // List<Ticket> findByMenuMenuIdAndUserUserIdAndTicketStatus(Long menuId, Long userId, TicketStatus ticketStatus);
}
