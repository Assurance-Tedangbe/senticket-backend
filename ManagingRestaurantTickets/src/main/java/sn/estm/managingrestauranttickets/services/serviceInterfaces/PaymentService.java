// Interface définissant les opérations de paiement disponibles

package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentInitiationDTO;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;


public interface PaymentService {

    /**
     * Initie un nouveau paiement avec l'API PayDunya.
     * Cette méthode:
     * 1. Sauvegarde le panier dans la table pending_payments
     * 2. Crée une facture sur les serveurs PayDunya
     * 3. Retourne l'URL de paiement
     *
     * @param request DTO contenant les informations du panier (userId, tickets, montant)
     * @return PaymentResponseDTO contenant l'URL de paiement et l'ID de transaction
     */
    PaymentResponseDTO initiatePayment(PaymentInitiationDTO request);

    /**
     * Confirme un paiement après le retour de PayDunya.
     * Cette méthode:
     * 1. Vérifie le statut du paiement auprès de PayDunya
     * 2. Récupère le panier sauvegardé
     * 3. Appelle le service d'achat de tickets pour créer les tickets
     *
     * @param transactionId Token PayDunya de la transaction
     */
    void confirmPayment(String transactionId);

    String checkPaymentStatus(String invoiceToken);
}
