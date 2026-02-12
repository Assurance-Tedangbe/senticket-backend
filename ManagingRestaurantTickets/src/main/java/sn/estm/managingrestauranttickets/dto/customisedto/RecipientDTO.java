package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipientDTO {

    @NotNull(message = "recipientId is required")
    private Long recipientId;

    @NotBlank(message = "recipientUsername is required")
    private String recipientUsername;
}
