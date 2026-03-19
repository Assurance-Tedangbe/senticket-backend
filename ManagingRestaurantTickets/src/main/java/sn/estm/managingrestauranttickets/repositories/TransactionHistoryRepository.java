package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;
import sn.estm.managingrestauranttickets.enumerations.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    /**
     * Retrieves list of transactionHistory by transactionType, startDate and endDtae
     * @param transactionType
     * @param startDate
     * @param endDate
     * @return
     */
   /* List<TransactionHistory> findByTransactionTypeAndDateBetween(
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate
    );*/

    /**
     * Récupère les transactions par type et intervalle de dates
     * @param transactionType le type de transaction (peut être null)
     * @param startDate date de début
     * @param endDate date de fin
     * @return liste des transactions correspondantes
     */
    @Query("SELECT t FROM TransactionHistory t WHERE " +
            "(:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.transactionDate DESC")
    List<TransactionHistory> findByTransactionTypeAndDateBetween(
            @Param("transactionType") TransactionType transactionType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Récupère toutes les transactions dans un intervalle de dates
     * @param startDate date de début
     * @param endDate date de fin
     * @return liste des transactions
     */
    List<TransactionHistory> findByTransactionDateBetweenOrderByTransactionDateDesc(
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
