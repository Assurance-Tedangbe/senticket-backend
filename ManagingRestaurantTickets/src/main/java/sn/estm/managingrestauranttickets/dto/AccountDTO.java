/**
 * Génères la classe AccountDTO correspondant à la classe Account en prenant
 * en compte tous les attributs, y compris les validations, les
 * annotations Lombok et l'association'
 **/
package sn.estm.managingrestauranttickets.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    
    private Long accountId;

    @NotBlank(message = "The account needs an account number.")
    @Size(min = 3, max = 70)
    private String accountNumber;

    @NotNull(message = "The balance is required.")
    private Double balance;

    @NotNull(message = "The creation date is required.")
    private LocalDate dateCreation;

    @Valid
    @NotNull(message = "The user is required.")
    private UserDTO userDTO;

}



