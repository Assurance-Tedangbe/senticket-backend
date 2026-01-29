package sn.estm.managingrestauranttickets.dto.customisedto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitPorterDTO {

    @NotNull(message = "User ID is required")
    private Long porterId;

    @NotBlank(message = "Username is required")
    private String porterUsername;
}
