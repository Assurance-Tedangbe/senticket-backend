/**
 * Génères la classe RoleDTO correspondant à la classe Role en prenant
 * en compte tous les attributs, y compris les validations, les
 * annotations Lombok et les l'attribut user.
 **/

package sn.estm.managingrestauranttickets.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long roleId;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom du rôle doit contenir entre 3 et 100 caractères")
    private String name;
    
    /**
     * A set of users associated with this role.
     * The collection is initialized as an empty {@link HashSet} to avoid {@code NullPointerException}
     * when adding or accessing users. Instead of using {@code @NotNull}, initializing the collection
     * ensures that it is always non-null, simplifying code that interacts with this field and
     * eliminating the need for null checks.
     *
     * @Builder.Default
     * private Set<UserDTO> userDTO = new HashSet<>();*/
}