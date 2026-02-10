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
public class SenderDTO {

    @NotNull(message = "senderId is required")
    private Long senderId;

    @NotBlank(message = "senderUsername is required")
    private String senderUsername;

    @NotBlank(message = "senderPassword is required")
    private String senderPassword;
}
