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
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PaymentService;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;


@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Initie un paiement PayDunya et retourne l'URL de paiement à Flutter
     *
     * Appelé par Flutter quand l'utilisateur clique sur "Payer"
     * Flutter enverra ensuite le body JSON avec les détails du paiement
     * En réponse, Flutter reçoit l'URL PayDunya et l'ouvre dans un WebView.
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
     * RÔLE : Afficher une page HTML simple de confirmation.
     *
     * PayDunya redirige le navigateur de l'utilisateur vers cette URL.
     * Cette URL est ouverte dans le WebView Flutter.
     *
     * IMPORTANT : NE PAS traiter le paiement ici.
     * - Le traitement réel se fait dans le webhook (serveur→serveur).
     * - Flutter détecte cette URL et ferme le WebView
     * - puis commence le polling sur /api/payments/status/{token}
     *
     * @param token Token PayDunya passé en paramètre GET par PayDunya (optionnel par sécurité)
     * @return 200 OK avec une page HTML de confirmation (visible dans le WebView)
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
     * IMPORTANT en développement :
     *   PayDunya ne peut pas appeler localhost. Il faut exposer le serveur via ngrok :
     *      → ngrok http 8080
     *      → Copier l'URL ngrok dans application.properties :
     *      paydunya.callback-url=https://xxx.ngrok.io/api/payments/webhook
     *
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
     * PayDunya redirige le navigateur vers cette URL si l'utilisateur
     * clique sur "Annuler" pendant le paiement.
     * Affichée dans le WebView Flutter, qui détecte l'URL et ferme le WebView.
     * Flutter affiche alors un message d'annulation à l'utilisateur.
     *
     * @return 200 OK avec une page HTML d'annulation
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
     * Endpoint de polling du statut de paiement pour Flutter.
     * Flutter appelle cet endpoint toutes les 5 secondes (polling)
     * après fermeture du WebView pour savoir si le paiement a été confirmé.
     * C'est le mécanisme de synchronisation entre le webhook (serveur→serveur)
     * et l'application Flutter.
     * Flux complet côté Flutter :
     *   1. Utilisateur paie dans le WebView
     *   2. WebView redirige vers /return → Flutter détecte et ferme le WebView
     *   3. Flutter commence le polling toutes les 5s sur cet endpoint
     *   4. Pendant ce temps, PayDunya appelle /webhook → paiement confirmé en BDD
     *   5. Quand cet endpoint retourne "completed", Flutter arrête le polling
     *    et affiche le message de succès à l'utilisateur
     *
     *  @param token Token PayDunya de la transaction à vérifier (dans l'URL path)
     *  @return 200 OK avec le statut
     */
    @GetMapping("/status/{token}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String token) {

        log.info("GET /api/payments/status/{}", token);

        // Interroge PayDunya directement pour avoir le statut le plus à jour
        String status = paymentService.checkPaymentStatus(token);

        return ResponseEntity.ok(status);
    }
}


