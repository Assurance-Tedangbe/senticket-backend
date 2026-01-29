package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.Valid;
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

    @Valid
    @NotNull(message = "The porter is required")
    private DebitPorterDTO debitPorterDTO;

    @Valid
    @NotNull(message = "The student is required")
    private DebitStudentDTO debitStudentDTO;

    @NotEmpty(message = "At least one ticket ID must be provided")
    private List<Long> ticketIds = new ArrayList<>();
}
