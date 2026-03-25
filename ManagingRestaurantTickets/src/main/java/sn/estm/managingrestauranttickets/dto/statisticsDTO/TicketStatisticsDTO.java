package sn.estm.managingrestauranttickets.dto.statisticsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketStatisticsDTO {

    // Statistiques par utilisateur (pour un étudiant spécifique)
    private Map<String, UserTicketStats> userStats;

    // Statistiques globales
    private GlobalTicketStats globalStats;

    // Statistiques des tickets disponibles
    private AvailableTicketsStats availableStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserTicketStats {
        private Long userId;
        private String username;
       /* private String firstName;
        private String lastName;*/
        private Integer purchasedTicketsCount;    // Nombre de tickets achetés
        private Integer debitedTicketsCount;       // Nombre de tickets débités
        private Integer totalTicketsCount;         // Total (achetés + débités)
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GlobalTicketStats {
        private Long totalPurchasedTickets;        // Total tickets achetés (tous utilisateurs)
        private Long totalDebitedTickets;          // Total tickets débités (tous comptes)
        private Long totalTicketsProcessed;        // Total tickets achetés + débités
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvailableTicketsStats {
        private Integer typeATicketsAvailable;     // Tickets Type A disponibles
        private Integer typeBTicketsAvailable;     // Tickets Type B disponibles
        private Integer totalTicketsAvailable;     // Total tickets disponibles (A + B)
    }
}
