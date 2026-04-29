// Implémentation du service de paiement avec OkHttp
// Gère les appels HTTP vers l'API PayDunya

package sn.estm.managingrestauranttickets.services.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.customisedto.PurchaseTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.PurchaseUserDTO;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentInitiationDTO;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentResponseDTO;
import sn.estm.managingrestauranttickets.entities.PendingPayment;
import sn.estm.managingrestauranttickets.paydunyaconfig.PayDunyaConfig;
import sn.estm.managingrestauranttickets.repositories.PendingPaymentRepository;
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

    private final PayDunyaConfig payDunyaConfig;
    private final PendingPaymentRepository pendingPaymentRepository;
    private final TicketService ticketService;
    private final ObjectMapper objectMapper;

    private OkHttpClient httpClient;

    @PostConstruct
    public void init() {
        // Configuration du client HTTP avec timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public PaymentResponseDTO initiatePayment(PaymentInitiationDTO request) {
        log.info("=== INITIATION PAIEMENT SENTICKET ===");
        log.info("User ID: {}, Montant: {} FCFA", request.getUserId(), request.getTotalAmount());

        try {
            // 1. Sauvegarde du panier
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

            // 2. Construction de la facture PayDunya - FORMAT CORRECT
            Map<String, Object> invoice = new HashMap<>();

            // Montant total (OBLIGATOIRE)
            invoice.put("total_amount", request.getTotalAmount());

            // Description (OBLIGATOIRE)
            invoice.put("description", "Achat de tickets Senticket");

            // Référence interne (optionnelle mais recommandée)
            invoice.put("invoice_id", pending.getId().toString());

            // 3. Ajout des articles - FORMAT CORRECT
            // Attention: PayDunya attend une Map avec des clés spécifiques
            List<Map<String, Object>> itemsList = new ArrayList<>();

            if (request.getCountA() > 0) {
                Map<String, Object> itemA = new HashMap<>();
                itemA.put("name", "Ticket Type A");
                itemA.put("quantity", request.getCountA());
                itemA.put("unit_price", 100.0);
                itemA.put("total_price", request.getCountA() * 100.0);
                itemA.put("description", "Ticket pour petit-déjeuner");
                itemsList.add(itemA);
            }

            if (request.getCountB() > 0) {
                Map<String, Object> itemB = new HashMap<>();
                itemB.put("name", "Ticket Type B");
                itemB.put("quantity", request.getCountB());
                itemB.put("unit_price", 150.0);
                itemB.put("total_price", request.getCountB() * 150.0);
                itemB.put("description", "Ticket pour déjeuner/dîner");
                itemsList.add(itemB);
            }

            invoice.put("items", itemsList);

            // 4. URLs de callback
            Map<String, String> actions = new HashMap<>();
            actions.put("return_url", payDunyaConfig.getReturnUrl());
            actions.put("cancel_url", payDunyaConfig.getCancelUrl());
            actions.put("callback_url", payDunyaConfig.getCallbackUrl());
            invoice.put("actions", actions);

            // 5. Envoi de la requête
            String jsonBody = objectMapper.writeValueAsString(invoice);
            log.info("Requête PayDunya: {}", jsonBody);

            // Construction de l'URL correcte
            String apiUrl = payDunyaConfig.getApiUrl() + "/checkout/invoice/create";
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

                if (response.isSuccessful()) {
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);
                    String transactionId = jsonResponse.get("token").asText();
                    String paymentUrl = jsonResponse.get("invoice_url").asText();

                    pending.setTransactionId(transactionId);
                    pendingPaymentRepository.save(pending);

                    return PaymentResponseDTO.builder()
                            .paymentUrl(paymentUrl)
                            .transactionId(transactionId)
                            .status("PENDING")
                            .message("Paiement initialisé")
                            .build();
                } else {
                    log.error("Erreur PayDunya - Status: {}, Body: {}", response.code(), responseBody);
                    throw new RuntimeException("Erreur PayDunya: " + responseBody);
                }
            }

        } catch (IOException e) {
            log.error("Erreur lors de l'appel PayDunya", e);
            throw new RuntimeException("Erreur technique: " + e.getMessage());
        }
    }

    /**
     * ÉTAPE 1: Initialisation du paiement
     * 1. Sauvegarde le panier dans pending_payments
     * 2. Construit la facture PayDunya
     * 3. Envoie la requête à PayDunya
     * 4. Retourne l'URL de paiement au frontend
     */
/*    @Override
    public PaymentResponseDTO initiatePayment(PaymentInitiationDTO request) {
        log.info("=== INITIATION PAIEMENT SENTICKET ===");
        log.info("User ID: {}, Montant: {} FCFA", request.getUserId(), request.getTotalAmount());
        log.info("Tickets A: {}, Tickets B: {}", request.getCountA(), request.getCountB());

        try {
            // ---------- 1. Sauvegarde du panier ----------
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
            log.info("Panier sauvegardé avec ID temporaire: {}", pending.getTransactionId());

            // ---------- 2. Construction de la facture PayDunya ----------
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("total_amount", request.getTotalAmount());
            invoice.put("description", "Achat de tickets Senticket");
            invoice.put("invoice_id", pending.getId().toString());

            // Ajout des articles
            Map<String, Object> items = new HashMap<>();
            if (request.getCountA() > 0) {
                Map<String, Object> itemA = new HashMap<>();
                itemA.put("name", "Ticket Type A");
                itemA.put("price", 100.0);
                itemA.put("quantity", request.getCountA());
                itemA.put("total_price", request.getCountA() * 100.0);
                itemA.put("description", "Ticket pour petit-déjeuner");
                items.put("Ticket Type A", itemA);
            }
            if (request.getCountB() > 0) {
                Map<String, Object> itemB = new HashMap<>();
                itemB.put("name", "Ticket Type B");
                itemB.put("price", 150.0);
                itemB.put("quantity", request.getCountB());
                itemB.put("total_price", request.getCountB() * 150.0);
                itemB.put("description", "Ticket pour déjeuner/dîner");
                items.put("Ticket Type B", itemB);
            }
            invoice.put("items", items);

            // URLs de callback
            Map<String, String> actions = new HashMap<>();
            actions.put("return_url", payDunyaConfig.getReturnUrl());
            actions.put("cancel_url", payDunyaConfig.getCancelUrl());
            actions.put("callback_url", payDunyaConfig.getCallbackUrl());
            invoice.put("actions", actions);

            // ---------- 3. Envoi à l'API PayDunya ----------
            String jsonBody = objectMapper.writeValueAsString(invoice);
            log.debug("Requête PayDunya: {}", jsonBody);

            Request payDunyaRequest = new Request.Builder()
                    .url(payDunyaConfig.getApiUrl() + "/checkout/invoice/create")
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .addHeader("PAYDUNYA-MASTER-KEY", payDunyaConfig.getMasterKey())
                    .addHeader("PAYDUNYA-PRIVATE-KEY", payDunyaConfig.getPrivateKey())
                    .addHeader("PAYDUNYA-PUBLIC-KEY", payDunyaConfig.getPublicKey())
                    .addHeader("PAYDUNYA-TOKEN", payDunyaConfig.getToken())
                    .build();

            try (Response response = httpClient.newCall(payDunyaRequest).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                log.info("Réponse PayDunya: {}", responseBody);

                if (!response.isSuccessful()) {
                    throw new RuntimeException("Erreur PayDunya: " + responseBody);
                }

                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                String transactionId = jsonResponse.get("token").asText();
                String paymentUrl = jsonResponse.get("invoice_url").asText();

                // ---------- 4. Mise à jour avec le vrai token ----------
                pending.setTransactionId(transactionId);
                pendingPaymentRepository.save(pending);
                log.info("Paiement initialisé - Transaction ID: {}", transactionId);

                return PaymentResponseDTO.builder()
                        .paymentUrl(paymentUrl)
                        .transactionId(transactionId)
                        .status("PENDING")
                        .message("Redirection vers la page de paiement")
                        .build();
            }

        } catch (IOException e) {
            log.error("Erreur lors de l'appel PayDunya", e);
            throw new RuntimeException("Erreur technique: " + e.getMessage());
        }
    }*/

    /**
     * ÉTAPE 2: Confirmation du paiement (callback après paiement réussi)
     * 1. Vérifie le statut auprès de PayDunya
     * 2. Récupère le panier sauvegardé
     * 3. APPELLE VOTRE SERVICE purchaseTickets() EXISTANT
     * 4. Met à jour le statut
     */
    @Override
    @Transactional
    public void confirmPayment(String transactionId) {
        log.info("=== CONFIRMATION PAIEMENT SENTICKET ===");
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

            // 3. Préparation des données pour votre service d'achat existant
            List<Long> ticketIds = Arrays.stream(pending.getTicketIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

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
    }

    /**
     * Vérifie le statut d'une transaction PayDunya
     */
    private String checkPaymentStatus(String transactionId) {
        try {
            Request request = new Request.Builder()
                    .url(payDunyaConfig.getApiUrl() + "/checkout/invoice/" + transactionId)
                    .get()
                    .addHeader("PAYDUNYA-MASTER-KEY", payDunyaConfig.getMasterKey())
                    .addHeader("PAYDUNYA-PRIVATE-KEY", payDunyaConfig.getPrivateKey())
                    .addHeader("PAYDUNYA-PUBLIC-KEY", payDunyaConfig.getPublicKey())
                    .build();

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
    }

    /**
     * Génère un ID temporaire avant la confirmation PayDunya
     */
    private String generateTempId() {
        return "TMP_" + System.currentTimeMillis();
    }
}

/*

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    // Configuration PayDunya injectée
    private final PayDunyaConfig payDunyaConfig;

    // Repository pour les paiements temporaires
    private final PendingPaymentRepository pendingPaymentRepository;

    // Client HTTP pour les appels à PayDunya
    private final OkHttpClient httpClient = new OkHttpClient();

    // Convertit les objets Java en JSON et vice-versa
    private final ObjectMapper objectMapper = new ObjectMapper();

    */
/**
     * ÉTAPE 1: Initialisation du paiement
     * - Sauvegarde la commande dans la base de données
     * - Envoie la facture à PayDunya
     * - Retourne l'URL de paiement au frontend
     *//*

    @Override
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO request) {
        log.info("=== INITIATION PAIEMENT ===");
        log.info("Utilisateur ID: {}", request.getUserId());
        log.info("Montant: {} FCFA", request.getAmount());

        try {
            // ---------- 1. Sauvegarde de la commande en attente ----------
            PendingPayment pendingPayment = PendingPayment.builder()
                    .transactionId(generateTransactionId()) // ID temporaire
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .build();

            pendingPayment = pendingPaymentRepository.save(pendingPayment);
            log.info("Commande sauvegardée avec ID: {}", pendingPayment.getId());

            // ---------- 2. Construction de la facture PayDunya ----------
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("total_amount", request.getAmount());
            invoice.put("description", request.getDescription());
            invoice.put("invoice_id", pendingPayment.getId().toString());

            // Ajout des articles à la facture
            Map<String, Object> items = new HashMap<>();
            if (request.getItems() != null) {
                for (PaymentItemDTO item : request.getItems()) {
                    Map<String, Object> itemDetails = new HashMap<>();
                    itemDetails.put("name", item.getName());
                    itemDetails.put("price", item.getPrice());
                    itemDetails.put("quantity", item.getQuantity());
                    itemDetails.put("description", item.getDescription());
                    items.put(item.getName(), itemDetails);
                }
            }
            invoice.put("items", items);

            // Ajout des informations client
            Map<String, String> customer = new HashMap<>();
            customer.put("name", request.getCustomerName() != null ?
                    request.getCustomerName() : "Client");
            customer.put("email", request.getCustomerEmail() != null ?
                    request.getCustomerEmail() : "");
            customer.put("phone", request.getCustomerPhone() != null ?
                    request.getCustomerPhone() : "");
            invoice.put("customer", customer);

            // URLs de callback
            Map<String, String> actions = new HashMap<>();
            actions.put("return_url", payDunyaConfig.getReturnUrl());
            actions.put("cancel_url", payDunyaConfig.getCancelUrl());
            actions.put("webhook_url", payDunyaConfig.getWebhookUrl());
            invoice.put("actions", actions);

            // ---------- 3. Envoi de la requête à PayDunya ----------
            String jsonBody = objectMapper.writeValueAsString(invoice);
            log.debug("Requête PayDunya: {}", jsonBody);

            Request payDunyaRequest = new Request.Builder()
                    .url(payDunyaConfig.getApiUrl() + "/checkout/invoice/create")
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .addHeader("PAYDUNYA-MASTER-KEY", payDunyaConfig.getMasterKey())
                    .addHeader("PAYDUNYA-PRIVATE-KEY", payDunyaConfig.getPrivateKey())
                    .addHeader("PAYDUNYA-PUBLIC-KEY", payDunyaConfig.getPublicKey())
                    .addHeader("PAYDUNYA-TOKEN", payDunyaConfig.getToken())
                    .build();

            Response response = httpClient.newCall(payDunyaRequest).execute();
            String responseBody = response.body().string();
            log.info("Réponse PayDunya: {}", responseBody);

            // ---------- 4. Traitement de la réponse ----------
            if (response.isSuccessful()) {
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                String transactionId = jsonResponse.get("token").asText();
                String paymentUrl = jsonResponse.get("invoice_url").asText();

                // Mise à jour du paiement avec l'ID de transaction PayDunya
                pendingPayment.setTransactionId(transactionId);
                pendingPaymentRepository.save(pendingPayment);

                log.info("Paiement initialisé avec succès - Transaction ID: {}", transactionId);

                return PaymentResponseDTO.builder()
                        .paymentUrl(paymentUrl)
                        .transactionId(transactionId)
                        .status("PENDING")
                        .message("Redirection vers la page de paiement")
                        .build();
            } else {
                log.error("Erreur PayDunya: {}", responseBody);
                throw new RuntimeException("Erreur lors de l'initiation du paiement");
            }

        } catch (IOException e) {
            log.error("Erreur technique lors de l'initiation", e);
            throw new RuntimeException("Erreur technique: " + e.getMessage());
        }
    }

    */
/**
     * ÉTAPE 2: Traitement du paiement réussi
     * Appelée par le callback /return après que l'utilisateur a payé
     *//*

    @Override
    @Transactional
    public void processSuccessfulPayment(String transactionId) {
        log.info("=== TRAITEMENT PAIEMENT RÉUSSI ===");
        log.info("Transaction ID: {}", transactionId);

        // 1. Vérifier le statut du paiement auprès de PayDunya
        String status = checkPaymentStatus(transactionId);

        if ("completed".equals(status)) {
            // 2. Récupérer la commande en attente
            PendingPayment pendingPayment = pendingPaymentRepository
                    .findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));

            // 3. Mettre à jour le statut
            pendingPayment.setStatus("COMPLETED");
            pendingPaymentRepository.save(pendingPayment);

            log.info("Paiement confirmé pour l'utilisateur {}", pendingPayment.getUserId());

            // TODO: Ici, vous pouvez appeler votre service d'achat de tickets
            // ticketService.createTicketsForUser(pendingPayment.getUserId(), ...);

        } else {
            log.warn("Paiement non complété - Statut: {}", status);
        }
    }

    */
/**
     * Vérifie le statut d'une transaction auprès de PayDunya
     * @param transactionId Identifiant de la transaction
     * @return Statut: "completed", "pending", "cancelled", "failed"
     *//*

    @Override
    public String checkPaymentStatus(String transactionId) {
        try {
            Request request = new Request.Builder()
                    .url(payDunyaConfig.getApiUrl() + "/checkout/invoice/" + transactionId)
                    .get()
                    .addHeader("PAYDUNYA-MASTER-KEY", payDunyaConfig.getMasterKey())
                    .addHeader("PAYDUNYA-PRIVATE-KEY", payDunyaConfig.getPrivateKey())
                    .addHeader("PAYDUNYA-PUBLIC-KEY", payDunyaConfig.getPublicKey())
                    .build();

            Response response = httpClient.newCall(request).execute();
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                return jsonResponse.get("status").asText();
            }

        } catch (IOException e) {
            log.error("Erreur vérification statut", e);
        }
        return "unknown";
    }

    */
/**
     * Génère un ID de transaction temporaire
     *//*

    private String generateTransactionId() {
        return "TMP_" + System.currentTimeMillis();
    }
}
*/


