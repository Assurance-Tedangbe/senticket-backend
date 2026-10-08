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

import java.util.Map;


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
    public ResponseEntity<String> webhook(
            @RequestBody(required = false) String rawBody,
            @RequestParam(required = false) Map<String, String> formParams) {

        log.info("POST /api/payments/webhook");

        // Déterminer le contenu selon le Content-Type
        String token = null;

        // ── CAS 1 : Form URL-encoded (envoi réel de PayDunya)
        // PayDunya envoie : data[invoice][token]=xxx
        if (formParams != null && !formParams.isEmpty()) {
            log.info("Webhook reçu en form-data: {} paramètres", formParams.size());

            // Chercher le token dans les paramètres form
            token = formParams.get("data[invoice][token]");

            if (token == null || token.isBlank()) {
                // Parcourir tous les paramètres pour trouver le token
                for (Map.Entry<String, String> entry : formParams.entrySet()) {
                    if (entry.getKey().contains("token") &&
                            entry.getKey().contains("invoice")) {
                        token = entry.getValue();
                        log.info("Token trouvé dans: {} = {}", entry.getKey(), token);
                        break;
                    }
                }
            }

            // Si toujours pas trouvé, décoder manuellement depuis rawBody
            if ((token == null || token.isBlank()) && rawBody != null) {
                token = extractTokenFromFormBody(rawBody);
            }
        }

        // ── CAS 2 : JSON (test via Postman)
        if ((token == null || token.isBlank()) && rawBody != null && !rawBody.isBlank()) {
            try {
                if (rawBody.trim().startsWith("{")) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(rawBody);

                    token = root.path("data").path("invoice").path("token").asText(null);
                    if (token == null || token.isBlank()) {
                        token = root.path("data").path("token").asText(null);
                    }
                    if (token == null || token.isBlank()) {
                        token = root.path("token").asText(null);
                    }
                }
            } catch (Exception e) {
                log.warn("Parsing JSON webhook échoué: {}", e.getMessage());
            }
        }

        if (token == null || token.isBlank()) {
            log.error("Token introuvable. formParams: {}, rawBody: {}",
                    formParams, rawBody != null ? rawBody.substring(0, Math.min(200, rawBody.length())) : "null");
            return ResponseEntity.ok("OK");
        }

        log.info("✅ Webhook - Token extrait: {}", token);

        try {
            paymentService.confirmPayment(token);
        } catch (Exception e) {
            log.error("Erreur confirmPayment: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok("OK");
    }

    /**
     * Extrait le token PayDunya depuis un body URL-encodé.
     * PayDunya envoie : data%5Binvoice%5D%5Btoken%5D=test_xxx
     * Décodé : data[invoice][token]=test_xxx
     */
    private String extractTokenFromFormBody(String body) {
        try {
            // Décoder l'URL encoding
            String decoded = java.net.URLDecoder.decode(body, "UTF-8");
            log.info("Body décodé (premiers 300 chars): {}",
                    decoded.substring(0, Math.min(300, decoded.length())));

            // Chercher data[invoice][token]=xxx
            String[] pairs = decoded.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    if (key.equals("data[invoice][token]") ||
                            key.contains("[invoice]") && key.contains("[token]")) {
                        log.info("Token trouvé dans body décodé: {}", value);
                        return value;
                    }
                }
            }

            // Chercher aussi data[status]=completed pour confirmer
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2 &&
                        keyValue[0].trim().equals("data[status]")) {
                    log.info("Status PayDunya: {}", keyValue[1]);
                }
            }

        } catch (Exception e) {
            log.error("Erreur décodage form body: {}", e.getMessage());
        }
        return null;
    }

    /*@PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody(required = false) String rawBody) {
        log.info("POST /api/payments/webhook - Body: {}", rawBody);

        if (rawBody == null || rawBody.isBlank()) {
            log.warn("Webhook reçu avec un body vide");
            return ResponseEntity.ok("OK");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawBody);

            String token = null;

            // Format PayDunya réel : { "data": { "invoice": { "token": "xxx" } } }
            if (token == null || token.isBlank()) {
                token = root.path("data").path("invoice").path("token").asText(null);
            }

            // Format alternatif 1 : { "data": { "token": "xxx" } }
            if (token == null || token.isBlank()) {
                token = root.path("data").path("token").asText(null);
            }

            // Format alternatif 2 : { "token": "xxx" }
            if (token == null || token.isBlank()) {
                token = root.path("token").asText(null);
            }

            // Format alternatif 3 : { "invoice": { "token": "xxx" } }
            if (token == null || token.isBlank()) {
                token = root.path("invoice").path("token").asText(null);
            }

            if (token == null || token.isBlank()) {
                log.error("Token introuvable dans le payload webhook: {}", rawBody);
                return ResponseEntity.ok("OK");
            }

            log.info("Webhook - Token extrait: {}", token);
            paymentService.confirmPayment(token);

        } catch (Exception e) {
            log.error("Erreur parsing webhook: {}", e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }*/

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


