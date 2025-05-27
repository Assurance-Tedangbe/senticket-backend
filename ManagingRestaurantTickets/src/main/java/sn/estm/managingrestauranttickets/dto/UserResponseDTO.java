package sn.estm.managingrestauranttickets.dto;

import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO {

    /* @NotEmpty, @NotNull, @Size, etc. are used on request DTOs not in response DTOs */

    String token;
    String username;
    Set<@Valid RoleDTO> roles;
    String userFirstName;
    String userLastName;
    String userEmailAddress;

}
