package sn.estm.managingrestauranttickets.dto.historydto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.enumerations.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryDTO {

    private Long id;

    @NotNull(message = "transactionType is required.")
    private TransactionType transactionType; // PURCHASE, DEBIT, TRANSFER

    @NotNull(message = "ticketsCount is required.")
    private Integer ticketsCount;

    @NotNull(message = "transferDate is required.")
    private LocalDateTime date;

    private List<Long> ticketIds;

    private String ticketSummary;

    private Double amount;

    // Spécifique aux ACHATS (PURCHASE)
    private UserDTO purchaserDTO; // L'acheteur des ticket(s)

    // Spécifique aux DÉBITS (DEBIT)
    private UserDTO porterDTO; // Le portier qui a effectué le débit
    private UserDTO studentDTO; // L'étudiant dont le compte a été débité

    // Spécifique aux TRANSFERTS (TRANSFER)
    private UserDTO senderDTO; // L'expéditeur du transfert
    private UserDTO recipientDTO; // Le destinataire du transfert
    private Boolean transferCanceled;
}
