package sn.estm.managingrestauranttickets.dto.customisedto.dtoforQRcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRValidationRequest {

    private String qrData; // The full JSON QR data from scanning

}
