package sn.estm.managingrestauranttickets.dto.customisedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreationTicketsRequestDTO {

    // Represents the total number of A tickets to create
    private Integer countA;

    // Represents the total number of B tickets to create
    private Integer countB;
}
