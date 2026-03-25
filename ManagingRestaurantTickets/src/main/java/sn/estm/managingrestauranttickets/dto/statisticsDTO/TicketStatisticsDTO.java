package sn.estm.managingrestauranttickets.dto.statisticsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO pour les statistiques des tickets
 * Contient toutes les informations statistiques nécessaires
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketStatisticsDTO {

    /**
     * Statistiques par utilisateur (pour chaque étudiant)
     * Clé = nom d'utilisateur, Valeur = statistiques de l'utilisateur
     */
    private Map<String, UserTicketStats> userStats;

    /**
     * Statistiques globales (tous utilisateurs confondus)
     */
    private GlobalTicketStats globalStats;

    /**
     * Statistiques des tickets disponibles (non achetés, non débités)
     */
    private AvailableTicketsStats availableStats;

    /**
     * Statistiques individuelles d'un utilisateur (étudiant)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserTicketStats {
        private Long userId;                    // ID de l'utilisateur
        private String username;                // Nom d'utilisateur
        private String firstName;               // Prénom
        private String lastName;                // Nom de famille
        private Integer purchasedTicketsCount;  // Nombre de tickets achetés
        private Integer debitedTicketsCount;    // Nombre de tickets débités
        private Integer totalTicketsCount;      // Total (achetés + débités)
    }

    /**
     * Statistiques globales des tickets
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GlobalTicketStats {
        private Long totalPurchasedTickets;     // Total tickets achetés (tous utilisateurs)
        private Long totalDebitedTickets;       // Total tickets débités (tous comptes)
        private Long totalTicketsProcessed;     // Total tickets achetés + débités
    }

    /**
     * Statistiques des tickets disponibles (status = AVAILABLE)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvailableTicketsStats {
        private Integer typeATicketsAvailable;   // Tickets Type A disponibles
        private Integer typeBTicketsAvailable;   // Tickets Type B disponibles
        private Integer totalTicketsAvailable;   // Total tickets disponibles (A + B)
    }
}
