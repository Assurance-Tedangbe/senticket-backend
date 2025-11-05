package sn.estm.managingrestauranttickets.dto.customisedto.dtoforQRcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.dto.TicketDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRValidationResult {

    private boolean valid;
    private Long studentAccountId;
    private String studentName;
    private BigDecimal accountBalance;
    private String message;
    private List<TicketDTO> availableTickets;

}
