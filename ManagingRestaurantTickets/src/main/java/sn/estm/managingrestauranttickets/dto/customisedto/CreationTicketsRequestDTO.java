package sn.estm.managingrestauranttickets.dto.customisedto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.dto.TicketDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreationTicketsRequestDTO {

    private List<TicketDTO> ticketDTO = new ArrayList<>();

    // Represents the total number of A tickets to create
    private Integer countA;

    // Represents the total number of B tickets to create
    private Integer countB;
}
