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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    // Dépendances injectées par Spring
    private final PayDunyaConfig payDunyaConfig;                      // Configuration PayDunya
    private final PendingPaymentRepository pendingPaymentRepository; // Repository pour les paiements temporaires
    private final TicketService ticketService;                       // Service existant pour les tickets
    private final ObjectMapper objectMapper;                        // Pour la conversion JSON
    private final UserRepository userRepository;
    private OkHttpClient httpClient;  // Client HTTP pour les appels vers PayDunya

    /**
     * Initialisation du client HTTP après la construction du bean.
     * Configure les timeouts pour éviter les appels bloquants.
     */
    @PostConstruct
    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        // Configuration DNS personnalisée
        try {
            builder.dns(hostname -> {
                // Si c'est sandbox.paydunya.com, utiliser une résolution manuelle
                if (hostname.equals("sandbox.paydunya.com")) {
                    try {
                        // Remplacer par l'IP actuelle de sandbox.paydunya.com
                        return Arrays.asList(InetAddress.getByAddress(new byte[]{104, 18, 10, 7}));
                    } catch (UnknownHostException e) {
                        // Fallback au DNS système
                        return Dns.SYSTEM.lookup(hostname);
                    }
                }
                return Dns.SYSTEM.lookup(hostname);
            });
        } catch (Exception e) {
            log.warn("Configuration DNS personnalisée impossible, utilisation du DNS système");
        }

        this.httpClient = builder.build();
    }
    /*@PostConstruct
    public void init() {
        // Configuration du client HTTP avec timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // Timeout de connexion
                .writeTimeout(30, TimeUnit.SECONDS)     // Timeout d'écriture
                .readTimeout(30, TimeUnit.SECONDS)      // Timeout de lecture
                .build();
    }*/

    /** ÉTAPE 1: INITIATION DU PAIEMENT
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
            String ticketIdsStr = request.getSelectedTicketIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

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
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            /// 3. Construction de l'objet STORE (Informations de la boutique)
            Map<String, String> store = new HashMap<>();
            store.put("name", "Senticket - Gestion de Tickets");
            store.put("tagline", "Achetez et transférez vos tickets facilement");
            store.put("postal_address", "Dakar, Sénégal");
            store.put("phone", "+221 77 766 99 94");
            store.put("logo_url", "https://senticket.sn/logo.png");
            store.put("website_url", "https://senticket.sn");

            /// 4. Construction des ARTICLES (items)
            /// PayDunya attend un objet avec des clés "item_1", "item_2", etc.
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
            List<String> channels = Arrays.asList(
                    "wave-senegal",           // Wave Sénégal
                    "orange-money-senegal"   // Orange Money Sénégal
            );

            /// 8. Construction de l'objet INVOICE (Facture)
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("total_amount", request.getTotalAmount());
            invoice.put("description", "Achat de tickets Senticket");
            invoice.put("items", items);
            invoice.put("taxes", taxes);
            invoice.put("customer", customer);
            invoice.put("channels", channels);

            /// 9. Construction des DONNÉES PERSONNALISÉES (custom_data)(optionnel)
            /// Ces données seront retournées dans le webhook
            Map<String, String> customData = new HashMap<>();
            customData.put("user_id", String.valueOf(request.getUserId()));
            customData.put("ticket_ids", ticketIdsStr);

            /// 10. Construction des ACTIONS (URLs de callback)
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
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            log.info("Requête PayDunya: {}", jsonBody);

            // URL CORRECTE pour le sandbox
            String apiUrl = "https://app.paydunya.com/sandbox-api/v1/checkout-invoice/create";
            log.info("URL PayDunya: {}", apiUrl);

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

                if (!response.isSuccessful()) {
                    throw new RuntimeException("Erreur PayDunya: " + responseBody);
                }

                /// Traitement de la réponse PayDunya - Vérification du succès et extraction des données
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                // Vérifier le code de réponse (00 = succès)
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.get("response_code").asText() : "";
                if (!"00".equals(responseCode)) {
                    String responseText = jsonResponse.has("response_text") ? jsonResponse.get("response_text").asText() : "Erreur inconnue";
                    throw new RuntimeException("PayDunya error: " + responseCode + " - " + responseText);
                }

                /// La réponse contient directement le token et l'URL
                // Récupérer le token (ID de transaction) directement dans la racine
                String transactionId = jsonResponse.get("token").asText();
                // L'URL de paiement est dans response_text pour ce format
                String paymentUrl = jsonResponse.get("response_text").asText();

                log.info("Transaction ID: {}, Payment URL: {}", transactionId, paymentUrl);

                // Mettre à jour le pending payment avec le vrai token
                pending.setTransactionId(transactionId);
                pendingPaymentRepository.save(pending);

                log.info("PAIEMENT INITIALISE - Transaction ID: {}, URL: {}", transactionId, paymentUrl);

                return PaymentResponseDTO.builder()
                        .paymentUrl(paymentUrl)
                        .transactionId(transactionId)
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
     * Cette méthode est appelée par le controller /api/payments/return
     * après que PayDunya a redirigé l'utilisateur.
     * Déroulement :
     * 1. Vérification du statut du paiement auprès de PayDunya
     * 2. Récupération du panier sauvegardé
     * 3. APPELLE VOTRE SERVICE purchaseTickets() EXISTANT
     * 4. Mise à jour du statut dans pending_payments
     */
    @Override
    @Transactional
    public void confirmPayment(String transactionId) {
        log.info("CONFIRMATION PAIEMENT SENTICKET ");
        log.info("Transaction ID: {}", transactionId);

        try {
            // 1. Vérification du statut auprès de PayDunya
            String status = checkPaymentStatus(transactionId);
            log.info("Statut PayDunya: {}", status);

            if (!"completed".equals(status)) {
                log.warn("Paiement non complété - Statut: {}", status);
                return;
            }

            // 2. Récupération du panier sauvegardé
            PendingPayment pending = pendingPaymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction non trouvée: " + transactionId));

            // 3. Récupérer les IDs des tickets
            List<Long> ticketIds = Arrays.stream(pending.getTicketIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            // 4. APPEL À LA NOUVELLE MÉTHODE DE TICKET SERVICE
            // Cette méthode contient toute la logique d'achat
            // C'est ici que les tickets sont réellement achetés et que l'historique est créé
            log.info("Appel du service d'achat de tickets pour l'utilisateur: {}", pending.getUserId());
            ticketService.executePurchase(pending.getUserId(), ticketIds);

            // 5. Mise à jour du statut
            pending.setStatus(PaymentStatus.COMPLETED);
            pendingPaymentRepository.save(pending);

            log.info("Paiement confirmé et tickets créés pour l'utilisateur: {}", pending.getUserId());

        } catch (Exception e) {
            log.error("Erreur lors de la confirmation du paiement", e);
            throw new RuntimeException("Erreur: " + e.getMessage());
        }
    }

    public String checkPaymentStatus(String invoiceToken) {
        try {
            // URL correcte : /checkout-invoice/confirm/{token}
            String url = payDunyaConfig.getApiUrl()
                    + "/checkout-invoice/confirm/" + invoiceToken;

            OkHttpClient client = new OkHttpClient();
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

                if (body.trim().startsWith("<")) {
                    log.error("PayDunya a retourné du HTML - URL incorrecte: {}", url);
                    return "unknown";
                }

                JsonNode root = new ObjectMapper().readTree(body);
                return root.path("invoice").path("status").asText("unknown");
            }

        } catch (Exception e) {
            log.error("Erreur vérification statut: {}", e.getMessage());
            return "unknown";
        }
    }

     /* @Override
    @Transactional
    public void confirmPayment(String transactionId) {
        log.info("CONFIRMATION PAIEMENT SENTICKET");
        log.info("Transaction ID: {}", transactionId);

        try {
            // 1. Vérification du statut auprès de PayDunya
            String status = checkPaymentStatus(transactionId);
            log.info("Statut PayDunya: {}", status);

            if (!"completed".equals(status)) {
                log.warn("Paiement non complété - Statut: {}", status);
                return;
            }

            // 2. Récupération du panier sauvegardé
            PendingPayment pending = pendingPaymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction non trouvée: "
                            + transactionId));

            // 3. Préparation des données pour votre service d'achat existant
            // Convertir la chaîne "1,2,3" en liste d'IDs
            List<Long> ticketIds = new ArrayList<>();
            if (pending.getTicketIds() != null && !pending.getTicketIds().isEmpty()) {
                for (String idStr : pending.getTicketIds().split(",")) {
                    ticketIds.add(Long.parseLong(idStr.trim()));
                }
            }
            *//* List<Long> ticketIds = Arrays.stream(pending.getTicketIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList()); *//*

            PurchaseTicketsRequestDTO purchaseRequest = PurchaseTicketsRequestDTO.builder()
                    .purchaseUserDTO(PurchaseUserDTO.builder()
                            .userId(pending.getUserId())
                            .build())
                    .selectedTicketIds(ticketIds)
                    .build();

            // 4. APPEL À VOTRE SERVICE D'ACHAT EXISTANT
            // C'est ici que les tickets sont réellement achetés et que l'historique est créé
            log.info("Appel du service d'achat de tickets pour l'utilisateur: {}", pending.getUserId());
            ticketService.purchaseTickets(purchaseRequest);

            // 5. Mise à jour du statut
            pending.setStatus("COMPLETED");
            pendingPaymentRepository.save(pending);

            log.info("Paiement confirmé et tickets créés pour l'utilisateur: {}", pending.getUserId());

        } catch (Exception e) {
            log.error("Erreur lors de la confirmation du paiement", e);
            throw new RuntimeException("Erreur: " + e.getMessage());
        }
    }*/

    /**
     * Vérifie le statut d'une transaction auprès de PayDunya
     * @param transactionId Token PayDunya de la transaction
     * @return Statut: "completed", "pending", "cancelled", "unknown"
     */
    /*private String checkPaymentStatus(String transactionId) {
        try {
            Request request = new Request.Builder()
                    .url(//payDunyaConfig.getApiUrl() +
                            "https://paydunya.com/sandbox-checkout/invoice/" + transactionId)
                    .get()
                    .addHeader("PAYDUNYA-MASTER-KEY", payDunyaConfig.getMasterKey())
                    .addHeader("PAYDUNYA-PRIVATE-KEY", payDunyaConfig.getPrivateKey())
                    .addHeader("PAYDUNYA-PUBLIC-KEY", payDunyaConfig.getPublicKey())
                    .build();

            // Vérifier statut GET: https://app.paydunya.com/sandbox-api/v1/checkout-invoice/confirm/{token}
           // https://app.paydunya.com/sandbox-api/v1/checkout-invoice/create
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonNode jsonResponse = objectMapper.readTree(response.body().byteStream());
                    return jsonResponse.get("status").asText();
                }
            }
        } catch (IOException e) {
            log.error("Erreur vérification statut", e);
        }
        return "unknown";
    }*/
}
