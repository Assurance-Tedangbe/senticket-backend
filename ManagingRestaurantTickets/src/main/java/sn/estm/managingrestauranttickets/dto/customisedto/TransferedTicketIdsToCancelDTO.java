package sn.estm.managingrestauranttickets.dto.customisedto;

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
public class TransferedTicketIdsToCancelDTO {

    @NotNull
    Long originalSenderAccountId;

    @NotNull
    Long currentOwnerAccountId;

    @NotEmpty(message = "List of idTickets can not be null")
    private List<Long> ticketIdsToCancel  = new ArrayList<>();
}
