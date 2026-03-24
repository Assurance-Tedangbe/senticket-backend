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
public class DebitHistoryDTO {

    private Long id;

    @Valid
    @NotNull(message = "tikcketDTO is required")
    private TicketDTO ticketDTO;

    @Valid
    @NotNull(message = "debitPorterDTO is required")
    private UserDTO debitPorterDTO;

    @Valid
    @NotNull(message = "debitStudentDTO is required")
    private UserDTO debitStudentDTO;

    @NotNull(message = "debitDate is required.")
    private LocalDateTime debitDate;
}
