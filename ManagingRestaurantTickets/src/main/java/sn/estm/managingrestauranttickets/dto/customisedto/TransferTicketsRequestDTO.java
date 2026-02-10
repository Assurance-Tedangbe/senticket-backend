package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.enumerations.TicketType;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferTicketsRequestDTO {

    @Valid
    @NotNull(message = "The porter is required")
    private SenderDTO senderDTO;

    @Valid
    @NotNull(message = "The student is required")
    private RecipientDTO recipientDTO;

    @NotNull(message = "Ticket type is required")
    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    @NotNull(message = "Number of tickets to transfer is required")
    @Min(value = 1, message = "At least one ticket must be transferred")
    private Integer numberOfTicketsToTransfer;
}
