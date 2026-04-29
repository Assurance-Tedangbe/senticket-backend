// Expose les endpoints REST pour le paiement
// Ces endpoints sont appelés par l'application Flutter

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
import sn.estm.managingrestauranttickets.dto.UserDTO;
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
     * Body: PaymentInitiationDTO
     * Response: PaymentResponseDTO avec l'URL PayDunya
     */
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Valid @RequestBody PaymentInitiationDTO request) {
        log.info("POST /api/payments/initiate - User: {}", request.getUserId());
        PaymentResponseDTO paymentResponseDTO = paymentService.initiatePayment(request);
        log.info("Payment initiation response: {}", paymentResponseDTO);
        return new ResponseEntity<>(paymentResponseDTO, HttpStatus.OK);
        //return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    /**
     * Callback de retour après paiement réussi (PAR - Paiement Avec Redirection)
     * PayDunya redirige l'utilisateur vers cette URL avec le token en paramètre
     *
     * GET /api/payments/return?token=xxx
     */
    @GetMapping("/return")
    public ResponseEntity<?> paymentReturn(@RequestParam("token") String token) {
        log.info("GET /api/payments/return - Transaction: {}", token);

        // Traiter le paiement réussi
        paymentService.confirmPayment(token);

        // Rediriger vers l'application mobile via deep linking
        String redirectUrl = "senticket://payment/success?transactionId=" + token;
        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    /**
     * Callback d'annulation de paiement
     * PayDunya redirige l'utilisateur vers cette URL s'il annule
     *
     * GET /api/payments/cancel?token=xxx
     */
    @GetMapping("/cancel")
    public ResponseEntity<?> paymentCancel(@RequestParam("token") String token) {
        log.info("GET /api/payments/cancel - Transaction: {}", token);

        String redirectUrl = "senticket://payment/cancel?transactionId=" + token;
        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    /**
     * Webhook pour les notifications IPN (Instant Payment Notification)
     * PayDunya envoie une requête POST sur cette URL automatiquement
     *
     * POST /api/payments/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/payments/webhook");

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        if (data != null) {
            String token = (String) data.get("token");
            String status = (String) data.get("status");

            log.info("Webhook - Transaction: {}, Statut: {}", token, status);

            if ("completed".equals(status)) {
                paymentService.confirmPayment(token);
            }
        }

        // Toujours retourner 200 OK pour que PayDunya arrête d'envoyer la notification
        return ResponseEntity.ok().build();
    }
}

/*@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    *//**
     * Endpoint pour initialiser un paiement
     * Appelé par l'application Flutter quand l'utilisateur clique sur "Payer"
     *
     * POST /api/payments/initiate
     * @param request DTO contenant les informations du panier
     * @return PaymentResponseDTO avec l'URL de paiement
     *//*
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Valid @RequestBody PaymentRequestDTO request) {

        log.info("POST /api/payments/initiate - Utilisateur: {}", request.getUserId());

        PaymentResponseDTO response = paymentService.initiatePayment(request);

        return ResponseEntity.ok(response);
    }

    *//**
     * Callback de retour après paiement réussi
     * PayDunya redirige l'utilisateur vers cette URL
     *
     * GET /api/payments/return?token=xxx
     * @param token Identifiant de la transaction PayDunya
     *//*
    @GetMapping("/return")
    public ResponseEntity<?> paymentReturn(@RequestParam("token") String token) {
        log.info("GET /api/payments/return - Transaction: {}", token);

        // Traiter le paiement réussi
        paymentService.processSuccessfulPayment(token);

        // Rediriger vers l'application mobile via deep linking
        String redirectUrl = "senticket://payment/success?transactionId=" + token;

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    *//**
     * Callback d'annulation de paiement
     * PayDunya redirige l'utilisateur vers cette URL s'il annule
     *
     * GET /api/payments/cancel?token=xxx
     *//*
    @GetMapping("/cancel")
    public ResponseEntity<?> paymentCancel(@RequestParam("token") String token) {
        log.info("GET /api/payments/cancel - Transaction: {}", token);

        String redirectUrl = "senticket://payment/cancel?transactionId=" + token;

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    *//**
     * Webhook pour les notifications automatiques
     * PayDunya appelle cette URL automatiquement quand le statut change
     *
     * POST /api/payments/webhook
     *//*
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/payments/webhook - Payload: {}", payload);

        String token = (String) payload.get("token");
        String status = (String) payload.get("status");

        if ("completed".equals(status)) {
            paymentService.processSuccessfulPayment(token);
        }

        return ResponseEntity.ok().build();
    }

    *//**
     * Endpoint pour vérifier le statut d'un paiement
     *
     * GET /api/payments/status/{transactionId}
     *//*
    @GetMapping("/status/{transactionId}")
    public ResponseEntity<Map<String, String>> getPaymentStatus(
            @PathVariable String transactionId) {

        String status = paymentService.checkPaymentStatus(transactionId);

        return ResponseEntity.ok(Map.of("status", status));
    }
}*/

/*
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
@Data
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaydunyaService paydunyaService;
    private final PaymentRepository paymentRepository;
    private final PaydunyaConfig config;

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiatePayment(@RequestBody PaymentRequest request) {
        PaydunyaInvoiceResponse response = paydunyaService.createInvoice(request);

        if ("00".equals(response.getResponseCode())) {
            Payment payment = new Payment();
            payment.setToken(response.getToken());
            payment.setOrderId(request.getOrderId());
            payment.setAmount(request.getAmount());
            payment.setCustomerName(request.getCustomerName());
            payment.setCustomerEmail(request.getCustomerEmail());
            payment.setCustomerPhone(request.getCustomerPhone());
            payment.setStatus("PENDING");
            paymentRepository.save(payment);

            return ResponseEntity.ok(Map.of(
                    "token", response.getToken(),
                    "invoiceUrl", response.getResponseText()
            ));
        }

        return ResponseEntity.badRequest().body(Map.of("error", response.getResponseText()));
    }

    @PostMapping("/ipn")
    public ResponseEntity<Void> handleIpn(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "PAYDUNYA-MASTER-KEY-HASH", required = false) String receivedHash) {

        // Vérification sécurité : le hash SHA-512 de votre MasterKey
        String expectedHash = DigestUtils.sha512Hex(config.getMasterKey());
        if (receivedHash == null || !expectedHash.equals(receivedHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Map<?, ?> data = (Map<?, ?>) payload.get("data");
            if (data == null) return ResponseEntity.badRequest().build();

            Map<?, ?> invoice = (Map<?, ?>) data.get("invoice");
            if (invoice == null) return ResponseEntity.badRequest().build();

            String token = (String) invoice.get("token");
            String status = (String) invoice.get("status"); // completed, pending, cancelled

            paymentRepository.findByToken(token).ifPresent(payment -> {
                switch (status) {
                    case "completed" -> payment.setStatus("SUCCESS");
                    case "cancelled"  -> payment.setStatus("CANCELLED");
                    case "failed"     -> payment.setStatus("FAILED");
                    default           -> payment.setStatus(status.toUpperCase());
                }
                paymentRepository.save(payment);
                // → Déclenchez ici votre logique métier (email, activation, etc.)
            });

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{token}")
    public ResponseEntity<Map<String, String>> checkStatus(@PathVariable String token) {
        // 1. Vérifier d'abord en base locale
        return paymentRepository.findByToken(token)
                .map(payment -> ResponseEntity.ok(Map.of(
                        "status", payment.getStatus(),
                        "orderId", payment.getOrderId()
                )))
                .orElseGet(() -> {
                    // 2. Sinon interroger directement PayDunya
                    PaydunyaConfirmResponse confirm = paydunyaService.confirmInvoice(token);
                    String status = confirm.getData() != null
                            ? confirm.getData().getInvoice().getStatus()
                            : "unknown";
                    return ResponseEntity.ok(Map.of("status", status));
                });
    }
}
*/
