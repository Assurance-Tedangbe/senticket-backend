package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelTransferTicketsRequestDTO {

    @Valid
    @NotNull(message = "The cancelTransferDTO is required")
    private CancelTransferDTO cancelTransferDTO;

    @NotEmpty(message = "At least one ticket ID must be provided")
    private List<Long> ticketIdsToCancel  = new ArrayList<>();
}
