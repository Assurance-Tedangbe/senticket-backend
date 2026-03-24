/* interface for Ticket Service */
package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CreationTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitAccountRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.PurchaseTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.enumerations.TicketType;


public interface TicketService {

    List<TicketDTO> createTickets(CreationTicketsRequestDTO creationTicketsRequestDTO);

    List<TicketDTO> readTickets();

    List<TicketDTO> purchaseTickets(PurchaseTicketsRequestDTO purchaseTicketsRequestDTO);

    void debitAccount(DebitAccountRequestDTO debitAccountRequestDTO);

    List<TicketDTO> getPurchasedTicketsByUser(Long userId, TicketType ticketType);

    TransactionHistoryDTO transferTickets(TransferTicketsRequestDTO transferTicketsRequestDTO);

    void cancelTransferTickets(CancelTransferTicketsRequestDTO cancelTransferTicketsRequestDTO);

    List<TicketDTO> readTicketsByUserId(Long userId);

    TicketDTO readTicketById(Long idTicket);

    /* void bookTicket(Long ticketId);

    TicketDTO updateTicket(TicketDTO ticketDTO);

    void deleteTicket(Long idTicket);

    void updateTicketStatus(Long ticketId, TicketStatus newStatus);

    List<TicketDTO> readTicketsByStatus(TicketStatus status);*/

}