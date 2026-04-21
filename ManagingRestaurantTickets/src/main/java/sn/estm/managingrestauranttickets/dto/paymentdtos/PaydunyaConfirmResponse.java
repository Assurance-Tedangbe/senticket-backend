/*
package sn.estm.managingrestauranttickets.dto.paymentdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaydunyaConfirmResponse {

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_text")
    private String responseText;

    @JsonProperty("status")
    private String status; // "completed", "pending", "cancelled", "failed"

    @JsonProperty("data")
    private PaydunyaConfirmData data;

    @Data
    public static class PaydunyaConfirmData {

        @JsonProperty("response_code")
        private String responseCode;

        @JsonProperty("response_text")
        private String responseText;

        @JsonProperty("hash")
        private String hash;

        @JsonProperty("invoice")
        private InvoiceData invoice;

        @JsonProperty("customer")
        private CustomerData customer;

        @JsonProperty("custom_data")
        private Object customData;
    }

    @Data
    public static class InvoiceData {

        @JsonProperty("token")
        private String token;

        @JsonProperty("total_amount")
        private String totalAmount;

        @JsonProperty("description")
        private String description;

        @JsonProperty("status")
        private String status; // "completed", "pending", "cancelled"

        @JsonProperty("receipt_url")
        private String receiptUrl;

        @JsonProperty("items")
        private Object items;

        @JsonProperty("taxes")
        private Object taxes;
    }

    @Data
    public static class CustomerData {

        @JsonProperty("name")
        private String name;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("email")
        private String email;
    }
}
*/
