/**
 * Génères la classe ConsulterMenuDTO correspondant à la classe Menu en prenant
 * en compte tous les attributs, y compris les validations, les
 * annotations Lombok et l'association avec d'autres entités.
 **/
package sn.estm.managingrestauranttickets.dto;

import java.time.LocalDate;

import jakarta.validation.Valid;
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
public class ConsulterMenuDTO {
    
    private Long consulterMenuId;

    @NotBlank(message = "The consultation date is required.")
    private LocalDate consultationDate;

    @Valid
    @NotNull(message = "The menu is required.")
    private MenuDTO menuDTO;

    @Valid
    @NotNull(message = "The user is required.")
    private UserDTO userDTO;

}