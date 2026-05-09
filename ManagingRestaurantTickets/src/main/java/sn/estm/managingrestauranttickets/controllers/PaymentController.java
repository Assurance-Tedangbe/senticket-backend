// Expose les endpoints REST pour l'intégration avec PayDunya.
// Ces endpoints sont appelés par:
// - L'application Flutter (pour initier un paiement)
// - PayDunya (pour les callbacks après paiement)

package sn.estm.managingrestauranttickets.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentInitiationDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PaymentService;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Endpoint pour initier un paiement
     * Appelé par l'application Flutter quand l'utilisateur clique sur "Payer"
     *
     * POST /api/payments/initiate
     * Body: PaymentInitiationDTO (userId, selectedTicketIds, totalAmount, countA, countB)
     * Response: PaymentResponseDTO avec l'URL PayDunya (paymentUrl, transactionId)
     */
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Valid @RequestBody PaymentInitiationDTO request) {

        log.info("Payment initiation running for User: {}", request.getUserId());

        PaymentResponseDTO paymentResponseDTO = paymentService.initiatePayment(request);

        log.info("Payment initiation response: {}", paymentResponseDTO);

        return new ResponseEntity<>(paymentResponseDTO, HttpStatus.OK);
    }

    /**
     * Callback de retour après paiement réussi (PAR - Paiement Avec Redirection)
     * PayDunya redirige l'utilisateur vers cette URL avec le token en paramètre
     *
     * GET /api/payments/return?token=xxx
     *   Cette méthode:
     *    1. Traite le paiement réussi
     *    2. Redirige vers l'application mobile via deep linking
     */
    @GetMapping("/return")
    public ResponseEntity<?> paymentReturn(@RequestParam("token") String token) {
        log.info("GET /api/payments/return - Transaction: {}", token);

        //  Confirmer et traiter le paiement réussi
        paymentService.confirmPayment(token);

        // Rediriger vers l'application mobile via deep linking
        // String redirectUrl = "senticket://payment/success?transactionId=" + token;
        String redirectUrl = "http://localhost:8080/api/payments/return?token=" + token;

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    /**
     * Webhook pour les notifications IPN (Instant Payment Notification)
     * PayDunya envoie une requête POST sur cette URL automatiquement
     * quand le statut du paiement change
     * Cela permet de traiter les paiements même si l'utilisateur ferme le navigateur.
     *
     * POST /api/payments/webhook
     * Body: payload contenant token et status dans la clé "data"
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/payments/webhook");

        // La structure des données reçues est dans la clé "data"
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        if (data != null) {
            String token = (String) data.get("token");
            String status = (String) data.get("status");

            log.info("Webhook - Transaction: {}, Statut: {}", token, status);

            // Traiter le paiement si complété
            if ("completed".equals(status)) {
                paymentService.confirmPayment(token);
            }
        }
        // Toujours retourner 200 OK pour que PayDunya arrête d'envoyer la notification
        return ResponseEntity.ok().build();
    }

    /**
     * Callback d'annulation de paiement.
     * PayDunya redirige l'utilisateur vers cette URL s'il annule le paiement.
     * GET /api/payments/cancel?token=xxx
     */
   /* @GetMapping("/cancel")
    public ResponseEntity<?> paymentCancel(@RequestParam("token") String token) {
        log.info("Requête d'annulation de paiement - TransactionID: {}", token);

        // Rediriger vers l'application mobile avec le statut annulé
        // String redirectUrl = "senticket://payment/cancel?transactionId=" + token;
        String redirectUrl = "http://localhost:8080/api/payments/cancel?token=" + token;

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }*/
}

