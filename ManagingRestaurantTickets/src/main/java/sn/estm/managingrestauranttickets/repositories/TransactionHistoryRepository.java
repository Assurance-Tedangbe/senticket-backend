package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;
import sn.estm.managingrestauranttickets.enumerations.TransactionType;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    /**
     * Méthode avec pagination pour tous les types
     * Récupère toutes les transactions dans un intervalle de dates
     * @param startDate date de début
     * @param endDate date de fin
     * @return liste des transactions
     */
    Page<TransactionHistory> findByDateBetweenOrderByDateDesc(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    /**
     * Méthode avec pagination pour un type spécifique
     * Récupère les transactions par type et intervalle de dates
     * @param transactionType le type de transaction (peut être null)
     * @param startDate date de début
     * @param endDate date de fin
     * @return liste des transactions correspondantes
     */
    Page<TransactionHistory> findByTransactionTypeAndDateBetween(
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT t FROM TransactionHistory t WHERE t.ticketIds LIKE %:ticketIds% " +
            "AND t.date BETWEEN :startDate AND :endDate")
    Optional<TransactionHistory> findByTicketIdsContainingAndDateBetween(
            @Param("ticketIds") String ticketIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

