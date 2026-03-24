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

    // Tickets concernés
    private List<Long> ticketIds;
    private List<String> ticketTypes;

    // Champs spécifiques aux ACHATS (PURCHASE)
    private UserDTO purchaserDTO; // L'acheteur des ticket(s)

    // Champs spécifiques aux DÉBITS (DEBIT)
    private UserDTO porterDTO; // Le portier qui a effectué le débit
    private UserDTO studentDTO; // L'étudiant dont le compte a été débité

    // Champs spécifiques aux TRANSFERTS (TRANSFER)
    private UserDTO senderDTO; // L'expéditeur du transfert
    private UserDTO recipientDTO; // Le destinataire du transfert
    private Boolean transferCanceled;

    // Méthodes utilitaires pour construire les DTOs selon le type
   /* public static TransactionHistoryDTO fromPurchase(
            Long id,
            LocalDateTime date,
            Integer ticketsCount,
            UserDTO purchaserDTO,
            List<Long> ticketIds,
            List<String> ticketTypes) {

        return TransactionHistoryDTO.builder()
                .id(id)
                .transactionType(TransactionType.PURCHASE)
                .date(date)
                .ticketsCount(ticketsCount)
                .purchaserDTO(purchaserDTO)
                .ticketIds(ticketIds)
                .ticketTypes(ticketTypes)
                .build();
    }

    public static TransactionHistoryDTO fromDebit(
            Long id,
            LocalDateTime date,
            Integer ticketsCount,
            UserDTO student,
            UserDTO porter,
            List<Long> ticketIds,
            List<String> ticketTypes) {

        return TransactionHistoryDTO.builder()
                .id(id)
                .transactionType(TransactionType.DEBIT)
                .date(date)
                .ticketsCount(ticketsCount)
                .studentDTO(student)
                .porterDTO(porter)
                .ticketIds(ticketIds)
                .ticketTypes(ticketTypes)
                .build();
    }

    public static TransactionHistoryDTO fromTransfer(
            Long id,
            LocalDateTime date,
            Integer ticketsCount,
            UserDTO sender,
            UserDTO recipient,
            List<Long> ticketIds,
            List<String> ticketTypes,
            Boolean canceled) {

        return TransactionHistoryDTO.builder()
                .id(id)
                .transactionType(TransactionType.TRANSFER)
                .date(date)
                .ticketsCount(ticketsCount)
                .senderDTO(sender)
                .recipientDTO(recipient)
                .ticketIds(ticketIds)
                .ticketTypes(ticketTypes)
                .transferCanceled(canceled)
                .build();
    }*/
}
