/* interface for Ticket Service */
package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitAccountRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.statisticsdto.TicketStatisticsDTO;
import sn.estm.managingrestauranttickets.enumerations.TicketType;


public interface TicketService {

    List<TicketDTO> readTickets();

    void debitAccount(DebitAccountRequestDTO debitAccountRequestDTO);

    List<TicketDTO> getPurchasedTicketsByUser(Long userId, TicketType ticketType);

    TransactionHistoryDTO transferTickets(TransferTicketsRequestDTO transferTicketsRequestDTO);

    void cancelTransferTickets(CancelTransferTicketsRequestDTO cancelTransferTicketsRequestDTO);

    /**
     * Récupère les statistiques des tickets
     * @param userId (optionnel) - ID de l'utilisateur pour les stats spécifiques
     * @return TicketStatisticsDTO contenant toutes les statistiques
     */
    TicketStatisticsDTO getTicketStatistics(Long userId);

    /**
     * Exécute l'achat de tickets (logique complète)
     * @return Liste des tickets achetés en DTO
     */
    List<TicketDTO> executePurchase(Long userId, int countA, int countB);
}