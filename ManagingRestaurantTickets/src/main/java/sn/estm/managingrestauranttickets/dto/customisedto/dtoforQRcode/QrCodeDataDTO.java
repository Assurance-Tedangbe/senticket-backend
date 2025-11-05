package sn.estm.managingrestauranttickets.dto.customisedto.dtoforQRcode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.exceptions.InvalidQRCodeException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeDataDTO {

    /** uniqueCode Purpose: Randomness & Uniqueness
     */
    @NotNull
    private Long accountId; //the account id of the user that the QR code has been scanned

    private String uniqueCode;  //🔒 Prevents replay attacks (random).ensures uniqueness

    private long timestamp;     //Generation time (Unix seconds)(prevents future/past use)

    private long expiresIn;     //How long valid: 300 seconds (5 minutes)

    /** signature Purpose: Integrity & Authentication
     */
    private String signature;   //🔒Prevents tampering,verifies authenticity.HMAC signature for security

    /**
     * Convert to JSON string for QR code encoding
     * Contains ALL validation data - STATELESS approach
     */
    public String toJsonString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting QR data to JSON", e);
        }
    }

    /**
     * Parse from JSON string (from scanned QR code)
     */
    public static QrCodeDataDTO fromJsonString(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, QrCodeDataDTO.class);
        } catch (Exception e) {
            throw new InvalidQRCodeException("Invalid QR code format");
        }
    }

    /**
     * Check if QR code is expired (STATELESS validation)
     */
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis() / 1000;
        return (currentTime - timestamp) > expiresIn;
    }
}
