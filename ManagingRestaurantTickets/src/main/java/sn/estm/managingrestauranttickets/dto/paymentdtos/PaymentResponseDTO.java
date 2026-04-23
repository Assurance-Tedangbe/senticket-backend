// Réponse envoyée au frontend après initialisation du paiement
// Contient l'URL de paiement PayDunya

package sn.estm.managingrestauranttickets.dto.paymentdtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    /** URL de la page de paiement PayDunya (à ouvrir dans le navigateur) */
    private String paymentUrl;

    /** Identifiant unique de la transaction PayDunya */
    private String transactionId;

    /** Statut du paiement: PENDING, SUCCESS, FAILED */
    private String status;

    /** Message explicatif pour l'utilisateur */
    private String message;
}
