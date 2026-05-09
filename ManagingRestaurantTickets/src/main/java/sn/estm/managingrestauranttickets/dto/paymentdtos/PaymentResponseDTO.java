// DTO réponse envoyée au frontend après initiation du paiement
// Contient l'URL de la page de paiement PayDunya

package sn.estm.managingrestauranttickets.dto.paymentdtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.enumerations.PaymentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    /** URL de la page de paiement PayDunya */
    private String paymentUrl;

    /** Identifiant unique de la transaction PayDunya */
    private String transactionId;

    /** Statut du paiement: PENDING, COMPLETED ou FAILED */
    private PaymentStatus status;

    /** Message explicatif pour l'utilisateur  */
    private String message;
}