/* interface for Ticket Service */
package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitAccountRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CreationTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.PurchaseTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;


public interface TicketService {

    List<TicketDTO> createTickets(CreationTicketsRequestDTO creationTicketsRequestDTO);

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

    List<TicketDTO> purchaseTickets(PurchaseTicketsRequestDTO purchaseTicketsRequestDTO);

    void transferTickets(TransferTicketsRequestDTO transferTicketsRequestDTO);

    void cancelTransferTickets(CancelTransferTicketsRequestDTO cancelTransferTicketsRequestDTO);

    void debitAccount(DebitAccountRequestDTO debitAccountRequestDTO);

}