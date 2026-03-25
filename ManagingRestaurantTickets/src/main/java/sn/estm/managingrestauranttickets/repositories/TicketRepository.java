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
    /**
     * Retrieves a list of tickets associated with the specified user
     */
    List<Ticket> findByUser(User user);

    /**
     * Retrieves list of tickets by user, ticketType, booked, ticketStatus
     * @param user
     * @param type
     * @param booked
     * @param status
     * @return
     */
    List<Ticket> findByUserAndTypeAndBookedAndStatus(
            User user,
            TicketType type,
            boolean booked,
            TicketStatus status
    );

    List<Ticket> findByUserAndBookedAndStatus(User user, boolean booked, TicketStatus status);
    List<Ticket> findByUserAndStatus(User user, TicketStatus status);
    List<Ticket> findByBookedAndStatus(boolean booked, TicketStatus status);

    /**
     * Retrieves a list of tickets that match the specified status.
     * @param status the status of the tickets to retrieve
     */
    List<Ticket> findByStatus(TicketStatus status);

    /*  Not used
       List<Ticket> findByBooked(boolean booked);
       List<Ticket> findByUserId(Long userId);

    *//**
     * Checks if a ticket with the specified status exists in the repository.
     * @param status the status to check for existence
     * @return {@code true} if a ticket with the given status exists, {@code false} otherwise
     *//*
    boolean existsByStatus(TicketStatus status);

    *//**
     * @param id the unique identifier of the ticket to find
     * @return an Optional containing the Ticket entity with the specified ticket ID if found, or an empty Optional if not found
     *//*
    Optional<Ticket> findById(Long id);

    *//**
     * @param userId the unique identifier of the user whose tickets are to be retrieved
     * @param status the status of the tickets to filter by (e.g., "ACTIVE", "USED", etc.)
     * @return a list of tickets objects matching the given user ID and status
     *//*
    List<Ticket> findByUserIdAndStatus(Long userId, TicketStatus status);
    */
}
