package sn.estm.managingrestauranttickets.dto.customisedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitRequestDTO {

    @NotNull(message = "Agent account ID is required")
    private Long portierAccountId;

    @NotNull(message = "Student account ID is required")
    private Long studentAccountId;

    @NotNull(message = "Ticket ID is required")
    private Long ticketId;
}
