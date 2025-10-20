package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import sn.estm.managingrestauranttickets.dto.AccountDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketFromDTO {

    @Valid
    @NotNull(message = "The user is required.")
    private UserDTO userDTO;

    @Valid
    @NotNull(message = "The account is required.")
    private AccountDTO accountDTO;

    @NotNull(message = "List of tickets is required.")
    private List<Long> selectedTicketIds;

}
