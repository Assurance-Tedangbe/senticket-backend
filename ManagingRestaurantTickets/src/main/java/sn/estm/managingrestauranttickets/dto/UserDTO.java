package sn.estm.managingrestauranttickets.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    private Long userId;

    @NotEmpty(message = "Username cannot be empty")
    String username;

    String password;

    @NotEmpty(message = "UserFirstName cannot be empty")
    String userFirstName;

    @NotEmpty(message = "UserLastname cannot be empty")
    String userLastName;

    @Email
    @NotEmpty(message = "Fill it out with your email")
    String userEmailAddress;

    @NotEmpty(message = "User must have at least one role")
    Set<@Valid RoleDTO> roles; //Valide les objets à l'intérieur de la collection (ici, chaque RoleDTO).
}
