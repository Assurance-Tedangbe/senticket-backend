package sn.estm.managingrestauranttickets.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequestDTO {

    @NotEmpty(message = "Username cannot be empty")
    private String username;
    private String password;

}
