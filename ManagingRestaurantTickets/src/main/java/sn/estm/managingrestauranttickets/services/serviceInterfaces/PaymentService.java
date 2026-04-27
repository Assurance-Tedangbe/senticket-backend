// Interface définissant les opérations de paiement disponibles

package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentInitiationDTO;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;


public interface PaymentService {

    /**
     * Initialise un paiement avec PayDunya
     * Sauvegarde le panier puis redirige vers PayDunya
     *
     * @param request DTO contenant les informations du panier
     * @return PaymentResponseDTO avec l'URL de paiement
     */
    PaymentResponseDTO initiatePayment(PaymentInitiationDTO request);

    /**
     * Confirme un paiement après retour de PayDunya
     * Appelle le service d'achat de tickets existant
     *
     * @param transactionId Token PayDunya de la transaction
     */
    void confirmPayment(String transactionId);
}

/*
public interface PaymentService {
    */
/**
     * Initialise un nouveau paiement auprès de PayDunya
     * @param request DTO contenant les informations du panier et du client
     * @return PaymentResponseDTO contenant l'URL de paiement
     *//*

    PaymentResponseDTO initiatePayment(PaymentRequestDTO request);

    */
/**
     * Traite le retour de PayDunya après un paiement réussi
     * @param transactionId Identifiant de la transaction PayDunya
     *//*

    void processSuccessfulPayment(String transactionId);

    */
/**
     * Vérifie le statut d'une transaction PayDunya
     * @param transactionId Identifiant de la transaction
     * @return Statut du paiement
     *//*

    String checkPaymentStatus(String transactionId);
}*/
