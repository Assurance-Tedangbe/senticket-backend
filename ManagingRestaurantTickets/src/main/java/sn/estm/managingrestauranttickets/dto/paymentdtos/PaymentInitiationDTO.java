// DTO  reçu du frontend pour initier un paiement.
// Contient toutes les informations nécessaires pour créer une facture PayDunya.
package sn.estm.managingrestauranttickets.dto.paymentdtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationDTO {

    /** ID de l'utilisateur qui effectue l'achat (doit être un étudiant) */
    @NotNull(message = "L'ID utilisateur est requis")
    private Long userId;

    /** IDs des tickets sélectionnés pour l'achat */
    @NotNull(message = "La liste des tickets est requise")
    private List<Long> selectedTicketIds;

    /** Montant total à payer (calculé côté frontend) */
    @NotNull(message = "Le montant est requis")
    @Positive(message = "Le montant doit être positif")
    private Double totalAmount;

    /** Nombre de tickets Type A à créer après l'achat (pour maintenir le stock) */
    private int countA;

    /** Nombre de tickets Type B à créer après l'achat (pour maintenir le stock) */
    private int countB;
}
