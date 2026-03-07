package sn.estm.managingrestauranttickets.dto.historydto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseHistoryDTO {

    private Long id;

    @Valid
    @NotNull(message = "tikcketDTO is required")
    private TicketDTO ticketDTO;

    @Valid
    @NotNull(message = "senderDTO is required")
    private UserDTO purchaseUserDTO;

    @NotNull(message = "transferDate is required.")
    private LocalDateTime purchaseDate;
}
