// Expose les endpoints REST pour l'intégration avec PayDunya.
// Ces endpoints sont appelés par:
// - L'application Flutter (pour initier un paiement)
// - PayDunya (pour les callbacks après paiement)

package sn.estm.managingrestauranttickets.controllers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentInitiationDTO;
import sn.estm.managingrestauranttickets.enumerations.PaymentStatus;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PaymentService;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;


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
     * Page de retour navigateur après paiement (return_url).
     *
     * PayDunya redirige le NAVIGATEUR de l'utilisateur ici après paiement.
     * Cette URL est ouverte dans le WebView Flutter.
     *
     * IMPORTANT : NE PAS traiter le paiement ici.
     * - Le traitement réel se fait dans le webhook (serveur→serveur)
     * - Flutter détecte cette URL et ferme le WebView
     * - Flutter continue le polling sur /api/payments/status/{token}
     *
     * GET /api/payments/return?token=xxx
     */
    @GetMapping("/return")
    public ResponseEntity<String> paymentReturn(
            @RequestParam(value = "token", required = false) String token) {

        log.info("GET /api/payments/return - Token: {}", token);

        // Retourner une simple page HTML : le WebView Flutter la détecte
        // et ferme le WebView pour laisser Flutter prendre le relai
        String html = """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Paiement effectué</title>
                    <style>
                        body { font-family: sans-serif; text-align: center; padding: 40px; }
                        h2 { color: #2e7d32; }
                    </style>
                </head>
                <body>
                    <h2>✓ Paiement reçu</h2>
                    <p>Votre paiement est en cours de traitement.</p>
                    <p>Retournez sur l'application Senticket.</p>
                </body>
                </html>
                """;

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }

    /**
     * Webhook IPN (Instant Payment Notification) PayDunya.
     *
     * PayDunya appelle cet endpoint automatiquement (serveur→serveur)
     * quand le paiement est confirmé par le réseau mobile money.
     *
     * C'est ICI que le paiement est traité :
     * tickets achetés par l'utilisateur, transaction enregistrée.
     *
     * Nécessite ngrok en développement (localhost non accessible depuis internet).
     *
     * POST /api/payments/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody(required = false) String rawBody) {

        log.info("POST /api/payments/webhook - Body: {}", rawBody);

        if (rawBody == null || rawBody.isBlank()) {
            log.warn("Webhook reçu avec un body vide");
            return ResponseEntity.ok("OK");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawBody);

            // PayDunya envoie le token dans différents champs selon la version API
            // Essayer plusieurs emplacements possibles dans le JSON
            String token = null;

            // Format 1 : { "data": { "token": "xxx" } }
            if (root.has("data")) {
                token = root.path("data").path("token").asText(null);
            }

            // Format 2 : { "token": "xxx" }
            if (token == null || token.isBlank()) {
                token = root.path("token").asText(null);
            }

            // Format 3 : { "invoice": { "token": "xxx" } }
            if (token == null || token.isBlank()) {
                token = root.path("invoice").path("token").asText(null);
            }

            if (token == null || token.isBlank()) {
                log.error("Token introuvable dans le payload webhook: {}", rawBody);
                return ResponseEntity.ok("OK"); // Toujours 200 pour éviter les retries PayDunya
            }

            log.info("Webhook - Token: {}", token);

            // Confirmer le paiement : vérifie le statut auprès de PayDunya
            // et attribue les tickets si le paiement est "completed"
            paymentService.confirmPayment(token);

        } catch (Exception e) {
            log.error("Erreur parsing webhook: {}", e.getMessage());
            // Retourner quand même 200 pour éviter que PayDunya réessaie en boucle
        }

        // PayDunya arrête d'envoyer le webhook seulement si on répond 200
        return ResponseEntity.ok("OK");
    }

    /**
     * Page d'annulation navigateur (cancel_url).
     * PayDunya redirige ici si l'utilisateur annule pendant le paiement.
     *
     * GET /api/payments/cancel
     */
    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {

        log.info("GET /api/payments/cancel");

        String html = """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <title>Paiement annulé</title>
                    <style>
                        body { font-family: sans-serif; text-align: center; padding: 40px; }
                        h2 { color: #c62828; }
                    </style>
                </head>
                <body>
                    <h2>✗ Paiement annulé</h2>
                    <p>Vous avez annulé le paiement.</p>
                    <p>Retournez sur l'application Senticket pour réessayer.</p>
                </body>
                </html>
                """;

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }

    /**
     * Endpoint de polling statut pour Flutter.
     * Flutter appelle cet endpoint toutes les 5 secondes
     * pour savoir si le paiement a été confirmé.
     *
     * GET /api/payments/status/{token}
     */
    @GetMapping("/status/{token}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String token) {

        log.info("GET /api/payments/status/{}", token);

        String status = paymentService.checkPaymentStatus(token);
        return ResponseEntity.ok(status);
    }
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
    /*@GetMapping("/return")
    public ResponseEntity<?> paymentReturn(@RequestParam("token") String invoice_token) {
        log.info("Callback de retour  - Transaction: {}", invoice_token);

        //  Confirmer et traiter le paiement réussi
        paymentService.confirmPayment(invoice_token);

        // Rediriger vers l'application mobile via deep linking
        String redirectUrl = "http://localhost:8080/api/payments/return?token=" + invoice_token;

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }*/

    /**
     * Webhook pour les notifications IPN (Instant Payment Notification)
     * PayDunya envoie une requête POST sur cette URL automatiquement
     * quand le statut du paiement change
     * Cela permet de traiter les paiements même si l'utilisateur ferme le navigateur.
     *
     * POST /api/payments/webhook
     * Body: payload contenant token et status dans la clé "data"
     */
    /*@PostMapping("/webhook")
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
    }*/

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

