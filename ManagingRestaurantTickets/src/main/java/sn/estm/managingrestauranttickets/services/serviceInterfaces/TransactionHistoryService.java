package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryService {

    List<TransactionHistoryResponseDTO> readTransactionHistory(
            String transactionType, LocalDateTime startDate, LocalDateTime  endDate, int page, int size
            );

}
