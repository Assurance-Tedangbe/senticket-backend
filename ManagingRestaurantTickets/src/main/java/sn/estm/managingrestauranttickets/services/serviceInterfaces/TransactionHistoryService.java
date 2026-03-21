package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;
import sn.estm.managingrestauranttickets.entities.User;

import java.time.LocalDate;
import java.util.List;

public interface TransactionHistoryService {

    TransactionHistoryResponseDTO readTransactionHistory(
            String transactionType,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size);

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

    TransactionHistory recordPurchase(User purchaseUser, List<Ticket> tickets );
    TransactionHistory recordDebit(User porter, User student, List<Ticket> tickets);
    TransactionHistory recordTransfer(User sender, User recipient, List<Ticket> tickets);
}
