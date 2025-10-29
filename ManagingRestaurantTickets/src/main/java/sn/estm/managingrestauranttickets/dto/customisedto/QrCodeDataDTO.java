package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeDataDTO {

    @NotNull
    private Long accountId; //the account id of the user that the QR code has been scanned

    private String uniqueCode;
}
