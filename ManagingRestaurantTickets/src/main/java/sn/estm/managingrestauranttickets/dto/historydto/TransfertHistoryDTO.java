package sn.estm.managingrestauranttickets.dto.historydto;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.dto.UserDTO;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransfertHistoryDTO {

    private Long id;

    private String ticketIdsTransfered;

    @Valid
    @NotNull(message = "senderDTO is required")
    private UserDTO senderDTO;

    @Valid
    @NotNull(message = "recipientDTO is required")
    private UserDTO recipientDTO;

    @NotNull(message = "transferDate is required.")
    private LocalDateTime transferDate;

    @Column(nullable = false)
    @NotNull(message = "canceled is required.")
    private boolean canceled;

    /*@Valid
    @NotNull(message = "tikcketDTO is required")
    private TicketDTO ticketDTO;*/
}
