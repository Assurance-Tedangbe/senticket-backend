package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransfertHistoryDTO;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;
import sn.estm.managingrestauranttickets.entities.User;

import java.time.LocalDate;
import java.util.List;

public interface TransactionHistoryService {
    /**
     * Récupère l'historique des transactions avec filtres
     * @param transactionType "PURCHASE", "DEBIT", "TRANSFER" ou "ALL"
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return TransactionHistoryResponseDTO
     */
    TransactionHistoryResponseDTO getTransactionHistory(
            String transactionType,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    );

    TransactionHistory recordPurchase(User purchaseUser, List<Ticket> tickets );
    TransactionHistory recordDebit(User porter, User student, List<Ticket> tickets);
    TransactionHistory recordTransfer(User sender, User recipient, List<Ticket> tickets);

    /**
     * Récupère l'historique des transactions pour un utilisateur spécifique
     * @param userId ID de l'utilisateur (portier pour ses débits, étudiant pour ses transactions)
     * @param transactionType "PURCHASE", "DEBIT", "TRANSFER" ou "ALL"
     * @param startDate Date de début (optionnelle)
     * @param endDate Date de fin (optionnelle)
     * @param page Numéro de la page
     * @param size Taille de la page
     * @return TransactionHistoryResponseDTO contenant les transactions paginées
     */
    TransactionHistoryResponseDTO getTransactionHistoryForUser(
            Long userId,
            String transactionType,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    );

    TransactionHistoryDTO readTransactionHistoryById(Long id);
}
