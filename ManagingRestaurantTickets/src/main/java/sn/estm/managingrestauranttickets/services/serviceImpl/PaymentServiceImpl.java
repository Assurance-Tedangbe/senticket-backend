// Implémentation du service de paiement avec OkHttp
// Utilise OkHttp pour les appels HTTP vers l'API PayDunya.

package sn.estm.managingrestauranttickets.services.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentInitiationDTO;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;
import sn.estm.managingrestauranttickets.entities.PendingPayment;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.PaymentStatus;
import sn.estm.managingrestauranttickets.paydunyaconfig.PayDunyaConfig;
import sn.estm.managingrestauranttickets.repositories.PendingPaymentRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.*;

import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    // Dépendances injectées par Spring
    private final PayDunyaConfig payDunyaConfig;
    // Contient les clés API PayDunya (masterKey, privateKey, publicKey, token)
    // et les URLs (apiUrl, returnUrl, cancelUrl, callbackUrl)
    // Chargées depuis application.properties via @ConfigurationProperties

    private final PendingPaymentRepository pendingPaymentRepository; // Repository pour les paiements temporaires
    private final TicketService ticketService;
    // Service existant pour la gestion des tickets
    // La méthode executePurchase() est appelée après confirmation du paiement
    // pour attribuer effectivement les tickets à l'utilisateur

    private final ObjectMapper objectMapper;
    // Convertisseur Java ↔ JSON fourni par Spring Boot (Jackson)
    // Utilisé pour sérialiser le payload envoyé à PayDunya et désérialiser la réponse

    private final UserRepository userRepository;
    // Accès à la table users
    // Utilisé pour récupérer le nom et l'email de l'acheteur (informations client PayDunya)

    private OkHttpClient httpClient;
    // Client HTTP OkHttp pour les appels vers l'API PayDunya
    // Non final car initialisé dans @PostConstruct (nécessite configuration des timeouts)

    /**
     * Initialisation du client HTTP après la construction du bean.
     * Configure les timeouts pour éviter les appels bloquants.
     *
     * @PostConstruct garantit que cette méthode est appelée après l'injection
     * de toutes les dépendances mais avant la première utilisation du service.
     *
     */
    @PostConstruct
    public void init() {
        // Configuration du client HTTP avec timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // Timeout de connexion. Temps max pour établir la connexion TCP
                .writeTimeout(30, TimeUnit.SECONDS)     // Timeout d'écriture. Temps max pour envoyer la requête
                .readTimeout(30, TimeUnit.SECONDS)      // Timeout de lecture. Temps max pour recevoir la réponse
                .build();
    }

    /** ÉTAPE 1: INITIATION DU PAIEMENT
     * Crée une facture PayDunya et retourne l'URL de paiement à Flutter
     * Déroulement:
     * 1. Sauvegarde du panier dans pending_payments
     * 2. Construction de la facture PayDunya (JSON)
     * 3. Envoi de la requête à l'API PayDunya
     * 4. Récupération de l'URL de paiement
     * 5. Retour de l'URL au frontend
     */
    @Transactional
    @Override
    public PaymentResponseDTO initiatePayment(PaymentInitiationDTO request) {
        log.info("INITIATION PAIEMENT SENTICKET ");
        log.info("User ID: {}, Montant: {} FCFA", request.getUserId(), request.getTotalAmount());

        try {
            /// 1. Sauvegarde du panier
            // Convertit la liste d'IDs de tickets en chaîne CSV : [1, 2] → "1,2"
            // Cette chaîne sera stockée dans la colonne ticket_ids de pending_payments
            String ticketIdsStr = request.getSelectedTicketIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // Crée un enregistrement temporaire dans pending_payments avec statut PENDING
            // Le transactionId est d'abord un ID temporaire (TMP_xxx)
            // Il sera remplacé par le vrai token PayDunya après la réponse de l'API
            PendingPayment pending = PendingPayment.builder()
                    .transactionId(generateTempId())
                    .userId(request.getUserId())
                    .ticketIds(ticketIdsStr)
                    .countA(request.getCountA())
                    .countB(request.getCountB())
                    .amount(request.getTotalAmount())
                    .build();
            pending = pendingPaymentRepository.save(pending);
            log.info("Panier sauvegardé avec ID: {}", pending.getId());

            /// 2. Récupération des informations utilisateur
            // Nécessaire pour renseigner le champ "customer" dans la facture PayDunya
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            /// 3. Construction de l'objet STORE
            // Informations de la boutique affichées sur la page de paiement PayDunya
            Map<String, String> store = new HashMap<>();
            store.put("name", "Senticket - Gestion de Tickets");
            store.put("tagline", "Achetez et transférez vos tickets facilement");
            store.put("postal_address", "Dakar, Sénégal");
            store.put("phone", "+221 77 766 99 94");
            store.put("logo_url", "https://senticket.sn/logo.png");
            store.put("website_url", "https://senticket.sn");

            /// 4. Construction des ARTICLES (items)
            // PayDunya attend un objet JSON avec des clés numérotées : "item_1", "item_2"
            // (pas un tableau JSON, mais un objet avec des clés string)
            Map<String, Object> items = new HashMap<>();
            int itemIndex = 1;

            if (request.getCountA() > 0) {
                Map<String, Object> itemA = new HashMap<>();
                itemA.put("name", "Ticket Type A");
                itemA.put("quantity", request.getCountA());
                itemA.put("unit_price", 100.0);
                itemA.put("total_price", request.getCountA() * 100.0);
                itemA.put("description", "Ticket pour petit-déjeuner");
                items.put("item_" + itemIndex++, itemA);
            }

            if (request.getCountB() > 0) {
                Map<String, Object> itemB = new HashMap<>();
                itemB.put("name", "Ticket Type B");
                itemB.put("quantity", request.getCountB());
                itemB.put("unit_price", 150.0);
                itemB.put("total_price", request.getCountB() * 150.0);
                itemB.put("description", "Ticket pour déjeuner/dîner");
                items.put("item_" + itemIndex++, itemB);
            }

            /// 5. Construction des TAXES (optionnel - vide si pas de taxes)
            Map<String, Object> taxes = new HashMap<>();
            // Si vous avez des taxes, ajoutez-les ici:
            // taxes.put("tva", Map.of("name", "TVA (18%)", "amount", request.getTotalAmount() * 0.18));

            /// 6. Construction du CLIENT
            Map<String, String> customer = new HashMap<>();
            customer.put("name", user.getFirstName() + " " + user.getLastName());
            customer.put("email", user.getEmail() != null ? user.getEmail() : "");
            customer.put("phone", "");

            /// 7. Configuration des CANAUX DE PAIEMENT (channels)(optionnel)
            /// Moyens de paiement disponibles pour le client
            // L'utilisateur choisira entre Wave et Orange Money sur la page PayDunya
            List<String> channels = Arrays.asList(
                    "wave-senegal",           // Wave Sénégal
                    "orange-money-senegal"   // Orange Money Sénégal
            );

            /// 8. Construction de l'objet INVOICE (Facture)
            // Objet principal de la requête PayDunya, contient tous les détails du paiement
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("total_amount", request.getTotalAmount());
            invoice.put("description", "Achat de tickets Senticket");
            invoice.put("items", items);
            invoice.put("taxes", taxes);
            invoice.put("customer", customer);
            invoice.put("channels", channels);

            /// 9. Construction des DONNÉES PERSONNALISÉES (custom_data)(optionnel)
            /// Ces données seront retournées par PayDunya dans le webhook
            // Permet d'identifier quel utilisateur et quels tickets sont concernés
            // sans avoir à maintenir un état côté serveur
            Map<String, String> customData = new HashMap<>();
            customData.put("user_id", String.valueOf(request.getUserId()));
            customData.put("ticket_ids", ticketIdsStr);

            /// 10. Construction des ACTIONS (URLs de callback)
            // return_url  : URL du navigateur après paiement réussi (ouverte dans le WebView)
            // cancel_url  : URL si l'utilisateur clique "Annuler" sur la page PayDunya
            // callback_url: URL appelée serveur→serveur par PayDunya après confirmation (IPN)
            // Doit être une URL publique (ngrok en développement)
            Map<String, String> actions = new HashMap<>();
            actions.put("return_url", payDunyaConfig.getReturnUrl());
            actions.put("cancel_url", payDunyaConfig.getCancelUrl());
            actions.put("callback_url", payDunyaConfig.getCallbackUrl());

            /// 11. Assemblage du corps de la requête COMPLET
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("store", store);
            requestBody.put("invoice", invoice);
            requestBody.put("custom_data", customData);
            requestBody.put("actions", actions);

            /// 12. Envoi de la requête à PayDunya
            // Sérialise le payload Java en JSON string
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            log.info("Requête PayDunya: {}", jsonBody);

            // URL CORRECTE pour le sandbox
            String apiUrl = "https://app.paydunya.com/sandbox-api/v1/checkout-invoice/create";
            log.info("URL PayDunya: {}", apiUrl);

            // Construction de la requête HTTP POST avec les headers d'authentification PayDunya
            // Les 4 headers sont obligatoires : MASTER-KEY, PRIVATE-KEY, PUBLIC-KEY, TOKEN
            Request payDunyaRequest = new Request.Builder()
                    .url(apiUrl)
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .addHeader("PAYDUNYA-MASTER-KEY", payDunyaConfig.getMasterKey())
                    .addHeader("PAYDUNYA-PRIVATE-KEY", payDunyaConfig.getPrivateKey())
                    .addHeader("PAYDUNYA-PUBLIC-KEY", payDunyaConfig.getPublicKey())
                    .addHeader("PAYDUNYA-TOKEN", payDunyaConfig.getToken())
                    .build();

            try (Response response = httpClient.newCall(payDunyaRequest).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                log.info("Status code: {}", response.code());
                log.info("Réponse PayDunya: {}", responseBody);

                // Si PayDunya retourne un code HTTP d'erreur (4xx, 5xx), lever une exception
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Erreur PayDunya: " + responseBody);
                }

                /// Traitement de la réponse PayDunya - Vérification du succès et extraction des données
                // Désérialise la réponse JSON PayDunya en un arbre d'objets JsonNode pour une manipulation facile
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                // Vérifie le code de réponse métier PayDunya : "00" = succès
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.get("response_code").asText() : "";
                if (!"00".equals(responseCode)) {
                    String responseText = jsonResponse.has("response_text") ? jsonResponse.get("response_text").asText() : "Erreur inconnue";
                    throw new RuntimeException("PayDunya error: " + responseCode + " - " + responseText);
                }

                /// La réponse contient directement le token et l'URL
                // Récupérer le token (identifiant unique de la transaction) depuis la réponse(racine)
                // Ce token sera utilisé pour vérifier le statut et retrouver le panier
                String transactionId = jsonResponse.get("token").asText();

                // Extrait l'URL de paiement retournée dans response_text
                // C'est cette URL que Flutter ouvrira dans le WebView
                String paymentUrl = jsonResponse.get("response_text").asText();

                log.info("Transaction ID: {}, Payment URL: {}", transactionId, paymentUrl);

                // Met à jour le pending payment en remplaçant le TMP_xxx par le vrai token PayDunya
                // Permet de retrouver ce panier quand le webhook arrivera avec ce token
                pending.setTransactionId(transactionId);
                pendingPaymentRepository.save(pending);

                log.info("PAIEMENT INITIALISE - Transaction ID: {}, URL: {}", transactionId, paymentUrl);

                // Retourne le DTO avec l'URL et le token à Flutter
                return PaymentResponseDTO.builder()
                        .paymentUrl(paymentUrl)             // URL PayDunya à ouvrir dans WebView
                        .transactionId(transactionId)       // Token pour le polling de statut
                        .status(PaymentStatus.PENDING)
                        .message("Redirection vers la page de paiement")
                        .build();
            }

        } catch (IOException e) {
            log.error("Erreur lors de l'appel PayDunya", e);
            throw new RuntimeException("Erreur technique: " + e.getMessage());
        }
    }

    /**
     * Génère un ID temporaire avant la confirmation PayDunya
     * @return ID temporaire (préfixé par TMP_)
     */
    private String generateTempId() {
        return "TMP_" + System.currentTimeMillis();
    }

    /**
     * ÉTAPE 2 : CONFIRMATION DU PAIEMENT (CALLBACK après paiement réussi)
     * Appelée par le webhook controller /api/payments/return quand PayDunya notifie un paiement réussi.
     * après que PayDunya a redirigé l'utilisateur.
     * Déroulement :
     * 1. Vérification du statut du paiement auprès de PayDunya
     * 2. Récupération du panier sauvegardé
     * 3. APPELLE VOTRE SERVICE purchaseTickets() EXISTANT
     * 4. Mise à jour du statut dans pending_payments
     *
     * * Finalise le paiement après confirmation de PayDunya.
     *      * Appelée par le webhook controller quand PayDunya notifie un paiement réussi.
     *      *
     *      * @Transactional : rollback automatique si l'achat de tickets ou la mise à jour
     *      * du statut échoue, évitant un paiement confirmé sans tickets attribués.
     */
    @Override
    @Transactional
    public void confirmPayment(String transactionId) {
        log.info("CONFIRMATION PAIEMENT SENTICKET ");
        log.info("Transaction ID: {}", transactionId);

        try {
            // 1. Vérification du statut auprès de PayDunya
            // Interroge directement l'API PayDunya pour obtenir le statut réel du paiement
            // Ne pas faire confiance uniquement au webhook : toujours vérifier le statut
            String status = checkPaymentStatus(transactionId);
            log.info("Statut PayDunya: {}", status);

            // Si le PaiementStatus n'est pas "completed", on arrête sans attribuer de tickets
            if (!"COMPLETED".equals(status)) {
                log.warn("Paiement non complété - Statut: {}", status);
                return;
            }

            // 2. Récupération du panier sauvegardé
            // Retrouve le panier créé lors de initiatePayment() grâce au token PayDunya
            PendingPayment pending = pendingPaymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction non trouvée: " + transactionId));

            // 3. Parsing des IDs de tickets
            // Reconvertit la chaîne CSV stockée en BDD en liste d'IDs Java
            // "1,2" → [1L, 2L]
            List<Long> ticketIds = Arrays.stream(pending.getTicketIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            // 4. EXÉCUTION DE L'ACHAT DE TICKETS
            // Cette méthode contient toute la logique d'achat de tickets :
            // Délègue à TicketService.executePurchase() qui :
            //   - Change le statut des tickets de AVAILABLE à BOOKED
            //   - Lie les tickets à l'utilisateur
            //   - Crée une entrée dans l'historique des transactions
            //   - Génère de nouveaux tickets disponibles en remplacement
            log.info("Appel du service d'achat de tickets pour l'utilisateur: {}", pending.getUserId());
            ticketService.executePurchase(pending.getUserId(), ticketIds);

            // 5. Mise à jour du statut
            // Marque le paiement comme COMPLETED dans pending_payments
            // Empêche un double traitement si le webhook est appelé plusieurs fois (idempotence)
            pending.setStatus(PaymentStatus.COMPLETED);
            pendingPaymentRepository.save(pending);

            log.info("Paiement confirmé et tickets créés pour l'utilisateur: {}", pending.getUserId());

        } catch (Exception e) {
            log.error("Erreur lors de la confirmation du paiement", e);
            throw new RuntimeException("Erreur: " + e.getMessage());
        }
    }

    /**
     * Vérifie le statut d'une transaction auprès de PayDunya
     * @param invoiceToken Token PayDunya de la transaction
     * @return Statut: "completed", "pending", "cancelled", "unknown"
     */
    public String checkPaymentStatus(String invoiceToken) {
        try {
            // Construction de l'URL de vérification PayDunya sandbox :
            // URL correcte : /checkout-invoice/confirm/{token}
            String url = payDunyaConfig.getApiUrl()
                    + "/checkout-invoice/confirm/" + invoiceToken;

            // Crée un nouveau client OkHttp (instance locale, pas le client partagé)
            // Acceptable pour cette méthode utilitaire peu fréquente
            OkHttpClient client = new OkHttpClient();

            // Construction de la requête GET avec les headers d'authentification
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("PAYDUNYA-MASTER-KEY", payDunyaConfig.getMasterKey())
                    .addHeader("PAYDUNYA-PRIVATE-KEY", payDunyaConfig.getPrivateKey())
                    .addHeader("PAYDUNYA-TOKEN", payDunyaConfig.getToken())    // ← clé API, PAS le token de facture
                    .addHeader("PAYDUNYA-PUBLIC-KEY", payDunyaConfig.getPublicKey())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String body = response.body() != null ? response.body().string() : "";
                log.info("Réponse statut PayDunya [{}]: {}", response.code(), body);

                // Si la réponse commence par "<", c'est du HTML (page d'erreur 404)
                // Cela indique une URL incorrecte
                if (body.trim().startsWith("<")) {
                    log.error("PayDunya a retourné du HTML - URL incorrecte: {}", url);
                    return String.valueOf(PaymentStatus.UNKNOWN);
                }

                // Parse la réponse JSON et extrait le statut du paiement
                JsonNode root = new ObjectMapper().readTree(body);
                return root.path("invoice").path("status").asText("UNKNOWN").toUpperCase();
            }

        } catch (Exception e) {
            log.error("Erreur vérification statut: {}", e.getMessage());
            return String.valueOf(PaymentStatus.UNKNOWN);
        }
    }
}
