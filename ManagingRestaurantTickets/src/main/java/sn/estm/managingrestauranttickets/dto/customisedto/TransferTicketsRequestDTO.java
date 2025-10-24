package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferTicketsRequestDTO {

    @NotNull
    Long fromAccountId;

    @NotNull
    Long toAccountId;

    @NotEmpty(message = "At least one ticket ID must be provided")
    private List<Long> selectedTicketIdsToTransfer  = new ArrayList<>();
}
