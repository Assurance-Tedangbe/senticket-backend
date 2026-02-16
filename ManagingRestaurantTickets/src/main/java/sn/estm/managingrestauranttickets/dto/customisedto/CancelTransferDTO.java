package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelTransferDTO {

    private Long transactionId;

    @Valid
    @NotNull(message = "originalSenderDTO is required")
    private SenderDTO originalSenderDTO;

    @Valid
    @NotNull(message = "currentOwnerDTO is required")
    private RecipientDTO currentOwnerDTO;
}
