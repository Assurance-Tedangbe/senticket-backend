package sn.estm.managingrestauranttickets.dto.customisedto.dtoforQRcode;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Component;

@Component
public class QRCodeCryptoSecurity {

    private final String SECRET_KEY = "your-32-byte-secret-key-for-qr-codes-2024";
   // private static final String HMAC_ALGORITHM = "HmacSHA256";

    /**
     * Generate HMAC signature for QR code data
     */
    public String generateSignature(QrCodeDataDTO data) {
        String payload = data.getAccountId() + ":" +
                data.getUniqueCode() + ":" +
                data.getTimestamp() + ":" +
                data.getExpiresIn();

        return HmacUtils.hmacSha256Hex(SECRET_KEY, payload);
    }

    /**
     * Verify HMAC signature of QR code data
     */
    public boolean verifySignature(QrCodeDataDTO data) {
        String expectedSignature = generateSignature(data);
        return expectedSignature.equals(data.getSignature());
    }

    /**
     * Create secured QR code data with signature
     */
    public QrCodeDataDTO createSecuredQRData(Long accountId, String uniqueCode, long expiresIn) {

        long timestamp = System.currentTimeMillis() / 1000;

        QrCodeDataDTO qrData = QrCodeDataDTO.builder()
                .accountId(accountId)
                .uniqueCode(uniqueCode)
                .timestamp(timestamp)
                .expiresIn(expiresIn)
                .build();

        // Add cryptographic signature
        String signature = generateSignature(qrData);
        qrData.setSignature(signature);

        return qrData;
    }
}
