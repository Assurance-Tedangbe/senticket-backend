package sn.estm.managingrestauranttickets.dto.customisedto.dtoforQRcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeResponse {

    private Long accountId;
    private String uniqueCode;
    private String qrCodeImageBase64;
    private Instant expiresAt;
    private Instant generatedAt;

  /*  private Long accountId;
    private String uniqueCode;
    private String qrCodeImageBase64;
    private Instant createdAt;
    private boolean isActive;
    private int totalScans;

    // Indicates if this is a new generation or existing QR
    private boolean isNewGeneration;*/
}
