/**
 * Génères la classe DebitDTO correspondant à la classe Debit en prenant
 * en compte tous les attributs, y compris les validations, les
 * annotations Lombok et les l'attributs accountId et userId.
 **/
package sn.estm.managingrestauranttickets.dto;

import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitDTO {
   
    private Long debitId;

    @NotNull(message = "Debit date is mandatory")
    private LocalDate debitDate;

    @NotNull(message = "Debit amount is mandatory")
    private Double debitAmount;

    @Valid
    @NotNull(message = "Account ID is mandatory")
    private AccountDTO accountDTO;

    @Valid
    @NotNull(message = "User ID is mandatory")
    private UserDTO userDTO;
}