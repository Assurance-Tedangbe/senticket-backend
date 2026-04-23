// Interface définissant les opérations de paiement disponibles

package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentRequestDTO;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;

public interface PaymentService {

    /**
     * Initialise un nouveau paiement auprès de PayDunya
     * @param request DTO contenant les informations du panier et du client
     * @return PaymentResponseDTO contenant l'URL de paiement
     */
    PaymentResponseDTO initiatePayment(PaymentRequestDTO request);

    /**
     * Traite le retour de PayDunya après un paiement réussi
     * @param transactionId Identifiant de la transaction PayDunya
     */
    void processSuccessfulPayment(String transactionId);

    /**
     * Vérifie le statut d'une transaction PayDunya
     * @param transactionId Identifiant de la transaction
     * @return Statut du paiement
     */
    String checkPaymentStatus(String transactionId);
}