package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitAccountRequestDTO {

    @NotNull(message = "Agent account ID is required")
    private Long portierAccountId;

    @NotNull(message = "Student account ID is required")
    private Long etudiantAccountId;

    @NotEmpty(message = "At least one ticket ID must be provided")
    private List<Long> ticketIds = new ArrayList<>();
}
