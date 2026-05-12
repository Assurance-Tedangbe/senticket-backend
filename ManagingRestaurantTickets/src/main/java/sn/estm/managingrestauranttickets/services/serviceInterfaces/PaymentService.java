// Interface définissant les opérations de paiement disponibles

package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentInitiationDTO;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;


public interface PaymentService {

    /**
     * Initie un nouveau paiement avec l'API PayDunya.
     * Flux complet :
     * 1. Sauvegarde le panier dans la table pending_payments
     * 2. Crée une facture sur les serveurs PayDunya
     * 3. Reçoit l'URL de paiement PayDunya
     * 4. Retourne l'URL de paiement à Flutter pour que l'utilisateur puisse finaliser le paiement
     * 5. Flutter redirige l'utilisateur vers l'URL de paiement dans un WebView intégré
     **
     * @param request DTO contenant les informations du panier (userId, tickets, montant)
     * @return PaymentResponseDTO contenant l'URL de paiement et l'ID de transaction
     */
    PaymentResponseDTO initiatePayment(PaymentInitiationDTO request);

    /**
     * Confirme un paiement après le retour (notification) de PayDunya.
     * Flux complet :
     * 1. Vérifie le statut du paiement auprès de PayDunya
     * 2. Récupère le panier sauvegardé
     * 3. Appel le service executePurchase pour l'achat des tickets
     * 4. Marque le Status de paiement comme COMPLETED
     *
     *  Idempotente : si le paiement est déjà COMPLETED, ne fait rien.
     *  Nécessite ngrok en développement (PayDunya appelle une URL publique).
     * @param transactionId Token PayDunya de la transaction
     */
    void confirmPayment(String transactionId);

    /**
     * Vérifie le statut d'une transaction directement auprès de l'API PayDunya.
     *
     * Appelle GET /checkout-invoice/confirm/{token} sur les serveurs PayDunya.
     * Utilisée dans deux cas :
     *   1. Par confirmPayment() pour valider que le paiement est bien "completed"
     *   2. Par le controller /status/{token} pour le polling Flutter
     *
     * Valeurs possibles retournées par PayDunya :"completed", "pending", "cancelled", "unknown"
     *
     * @param invoiceToken Token PayDunya de la facture à vérifier
     * @return Statut du paiement sous forme de String
     */
    String checkPaymentStatus(String invoiceToken);
}
