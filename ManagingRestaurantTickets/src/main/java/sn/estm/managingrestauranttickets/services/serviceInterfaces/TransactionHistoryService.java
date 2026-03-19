package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryService {

    List<TransactionHistoryResponseDTO> readTransactionHistory(
            String transactionType, LocalDateTime startDate, LocalDateTime  endDate, int page, int size
            );

    /**
     * Récupère l'historique des transactions avec filtres
     * @param transactionType "PURCHASE", "DEBIT", "TRANSFER" ou "ALL"
     * @param startDate date de début (YYYY-MM-DD)
     * @param endDate date de fin (YYYY-MM-DD)
     * @return liste des transactions
     */
    List<TransactionHistoryDTO> getTransactionHistory(
            String transactionType,
            LocalDate startDate,
            LocalDate endDate
    );

}
