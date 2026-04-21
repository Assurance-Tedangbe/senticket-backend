/*
package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaydunyaConfirmResponse;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaydunyaInvoiceResponse;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentRequest;
import sn.estm.managingrestauranttickets.entities.Payment;
import sn.estm.managingrestauranttickets.repositories.PaymentRepository;
import sn.estm.managingrestauranttickets.paydunyaconfig.PaydunyaConfig;
import sn.estm.managingrestauranttickets.services.PaydunyaService;

import java.util.Map;

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
