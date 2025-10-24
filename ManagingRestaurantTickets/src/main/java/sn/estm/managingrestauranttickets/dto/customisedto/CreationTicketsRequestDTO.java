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

    private Integer countA;

    private Integer countB;
}
