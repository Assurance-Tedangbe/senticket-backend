package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import sn.estm.managingrestauranttickets.dto.AccountDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseTicketsRequestDTO {

    @Valid
    @NotNull(message = "The user is required.")
    private PurchaseUserDTO purchaseUserDTO;

    @NotNull(message = "At least one ticket must be selected")
    private List<Long> selectedTicketIds = new ArrayList<>();

}
