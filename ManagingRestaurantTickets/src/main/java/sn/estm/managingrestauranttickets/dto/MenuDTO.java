package sn.estm.managingrestauranttickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDTO {
    
    private Long menuId;

    @NotBlank(message = "The menu needs a name.")
    @Size(min = 3, max = 50)
    private String menuName;

    @NotNull(message = "The menu price is required.")
    private Double menuPrice;

    @NotBlank(message = "The menu needs a description.")
    @Size(min = 3, max = 100)
    private String menuDescription;
    
}