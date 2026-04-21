/*
package sn.estm.managingrestauranttickets.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaydunyaConfirmResponse;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaydunyaInvoiceResponse;
import sn.estm.managingrestauranttickets.dto.paymentdtos.PaymentRequest;
import sn.estm.managingrestauranttickets.paydunyaconfig.PaydunyaConfig;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaydunyaService {

    private final PaydunyaConfig config;
    private final RestTemplate restTemplate;

    public PaydunyaInvoiceResponse createInvoice(PaymentRequest request) {
        String url = config.getBaseUrl() + "/checkout-invoice/create";
        HttpHeaders headers = buildHeaders();

        Map<String, Object> store = new HashMap<>();
        store.put("name", config.getStoreName());

        Map<String, Object> customer = new HashMap<>();
        customer.put("name", request.getCustomerName());
        customer.put("email", request.getCustomerEmail());
        customer.put("phone", request.getCustomerPhone());

        Map<String, Object> invoice = new HashMap<>();
        invoice.put("total_amount", request.getAmount());
        invoice.put("description", request.getDescription());
        invoice.put("customer", customer);

        Map<String, Object> actions = new HashMap<>();
        actions.put("return_url", config.getReturnUrl() + "?orderId=" + request.getOrderId());
        actions.put("cancel_url", config.getCancelUrl());
        actions.put("callback_url", config.getCallbackUrl());

        Map<String, Object> customData = new HashMap<>();
        customData.put("order_id", request.getOrderId());

        Map<String, Object> body = new HashMap<>();
        body.put("store", store);
        body.put("invoice", invoice);
        body.put("actions", actions);
        body.put("custom_data", customData);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<PaydunyaInvoiceResponse> response = restTemplate.postForEntity(
                url, entity, PaydunyaInvoiceResponse.class
        );

        return response.getBody();
    }

    public PaydunyaConfirmResponse confirmInvoice(String token) {
        String url = config.getBaseUrl() + "/checkout-invoice/confirm/" + token;
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PaydunyaConfirmResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, PaydunyaConfirmResponse.class
        );

        return response.getBody();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("PAYDUNYA-MASTER-KEY", config.getMasterKey());
        headers.set("PAYDUNYA-PRIVATE-KEY", config.getPrivateKey());
        headers.set("PAYDUNYA-PUBLIC-KEY", config.getPublicKey());
        headers.set("PAYDUNYA-TOKEN", config.getToken());
        return headers;
    }
}
*/
