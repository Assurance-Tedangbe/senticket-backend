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
    @Size(min = 3, max = 70)
    private String menuName;

    @NotNull(message = "The menu type is required.")
    @Size(min = 3, max = 30)
    private String menuType;

    @NotBlank(message = "The menu needs a description.")
    @Size(min = 3, max = 100)
    private String menuDescription;
    
}