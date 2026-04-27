// DTO reçu du frontend pour initier un paiement
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

    /** ID de l'utilisateur qui effectue l'achat */
    @NotNull
    private Long userId;

    /** IDs des tickets sélectionnés pour l'achat */
    @NotNull
    private List<Long> selectedTicketIds;

    /** Montant total à payer */
    @NotNull
    @Positive
    private Double totalAmount;

    /** Nombre de tickets Type A à créer après achat */
    private int countA;

    /** Nombre de tickets Type B à créer après achat */
    private int countB;
}