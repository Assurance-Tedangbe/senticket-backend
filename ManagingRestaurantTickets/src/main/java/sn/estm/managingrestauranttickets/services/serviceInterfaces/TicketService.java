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
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentInitiationDTO;
import sn.estm.managingrestauranttickets.dto.statisticsDTO.TicketStatisticsDTO;
import sn.estm.managingrestauranttickets.enumerations.TicketType;


public interface TicketService {

    List<TicketDTO> createTickets(CreationTicketsRequestDTO creationTicketsRequestDTO);

    List<TicketDTO> readTickets();

    List<TicketDTO> purchaseTickets(PurchaseTicketsRequestDTO purchaseTicketsRequestDTO);

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
     * Cette méthode est réutilisable et contient TOUTE la logique d'achat
     *
     * @param userId ID de l'utilisateur
     * @param ticketIds IDs des tickets à acheter
     * @return Liste des tickets achetés en DTO
     */
    List<TicketDTO> executePurchase(Long userId, List<Long> ticketIds);

    /**
     * Prépare un paiement (calcule le total et les quantités)
     * Appelée par le frontend avant la redirection vers PayDunya
     *
     * @param userId ID de l'utilisateur
     * @param ticketIds IDs des tickets à acheter
     * @return PaymentInitiationDTO contenant les informations pour PayDunya
     */
    PaymentInitiationDTO preparePayment(Long userId, List<Long> ticketIds);
}