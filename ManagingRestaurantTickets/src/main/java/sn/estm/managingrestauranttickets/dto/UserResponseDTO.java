package sn.estm.managingrestauranttickets.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO {

    /* @NotEmpty, @NotNull, @Size, etc. are used on request DTOs not in response DTOs */

    String token;
    String username;
    RoleDTO role;
    String firstName;
    String lastName;
    String email;

}
