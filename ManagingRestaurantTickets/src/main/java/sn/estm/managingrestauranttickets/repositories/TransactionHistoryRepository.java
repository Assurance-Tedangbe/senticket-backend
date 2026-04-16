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

    /**
     * Méthode pour récupérer les transactions d'un utilisateur spécifique
     * @param userId    ID de l'utilisateur
     * @param startDate Date de début
     * @param endDate   Date de fin
     * @param pageable  Paramètres de pagination
     * @return Page de transactions
     */
    @Query("SELECT t FROM TransactionHistory t WHERE " +
            "(t.purchaser.id = :userId OR " +
            "t.student.id = :userId OR " +
            "t.sender.id = :userId OR " +
            "t.recipient.id = :userId OR " +
            "t.porter.id = :userId) AND " +
            "t.date BETWEEN :startDate AND :endDate " +
            "ORDER BY t.date DESC")
    Page<TransactionHistory> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Récupère les transactions d'un utilisateur avec filtrage par type
     * @param userId    ID de l'utilisateur
     * @param type      Type de transaction
     * @param startDate Date de début
     * @param endDate   Date de fin
     * @param pageable  Paramètres de pagination
     * @return Page de transactions
     */
    @Query("SELECT t FROM TransactionHistory t WHERE " +
            "(t.purchaser.id = :userId OR " +
            "t.student.id = :userId OR " +
            "t.sender.id = :userId OR " +
            "t.recipient.id = :userId OR " +
            "t.porter.id = :userId) AND " +
            "t.transactionType = :type AND " +
            "t.date BETWEEN :startDate AND :endDate " +
            "ORDER BY t.date DESC")
    Page<TransactionHistory> findByUserIdAndTransactionTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Récupère les transactions d'un portier (uniquement les débits qu'il a effectués)
     * @param porterId  ID du portier
     * @param startDate Date de début
     * @param endDate   Date de fin
     * @param pageable  Paramètres de pagination
     * @return Page de transactions de type DEBIT
     */
   /* @Query("SELECT t FROM TransactionHistory t WHERE " +
            "t.porter.id = :porterId AND " +
            "t.transactionType = com.senticket.enums.TransactionType.DEBIT")
    Page<TransactionHistory> findByPorterIdAndDateBetween(
            @Param("porterId") Long porterId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable); */
}

