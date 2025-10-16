/* interface for Ticket Service */
package sn.estm.managingrestauranttickets.services.serviceInterfaces;
import java.util.List;
import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;


public interface TicketService {
        
    TicketDTO createTicket(TicketDTO ticketDTO);

    List<TicketDTO> readTickets();

    TicketDTO readTicketById(Long idTicket);

    TicketDTO updateTicket(TicketDTO ticketDTO);

    void deleteTicket(Long idTicket);

    void updateTicketStatus(Long ticketId, TicketStatus newStatus);

    void bookTicket(Long ticketId);

    void unbookTicket(Long ticketId);

    List<TicketDTO> readTicketsByStatus(TicketStatus status);

    List<TicketDTO> readTicketsByAccountId(Long accountId);

    List<TicketDTO> readTicketsByUserId(Long userId);

    /**
     * Retrieves a list of tickets associated with a specific menu and user.
     *
     * @param menuId the ID of the menu to filter tickets by
     * @param userId the ID of the user to filter tickets by
     * @return a list of {@link TicketDTO} objects matching the specified menu and user
     */
    List<TicketDTO> readTicketsByMenuIdAndUserId(Long menuId, Long userId);

/**
 * Retrieves a list of tickets filtered by the specified menu ID, user ID, and ticket status.
 *
 * @param menuId the ID of the menu to filter tickets by
 * @param userId the ID of the user to filter tickets by
 * @param status the status of the tickets to filter by
 * @return a list of {@link TicketDTO} objects matching the given criteria
 */
    List<TicketDTO> readTicketsByMenuIdAndUserIdAndStatus(Long menuId, Long userId, TicketStatus status);

    void purchaseTicket(Long accountId, TicketDTO ticketDTO);

    void transferTicket(Long fromAccountId, Long toAccountId, Long ticketId);

    void cancelTransferTicket(Long fromAccountId, Long toAccountId, Long ticketId);

    void debitAccount(Long accountId, Long ticketId, Long debiterUserId);

}