// Interface pour les opérations CRUD sur les paiements temporaires

package sn.estm.managingrestauranttickets.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.entities.PendingPayment;

import java.util.Optional;

@Repository
public interface PendingPaymentRepository extends JpaRepository<PendingPayment, Long> {

    /**
     * Recherche un paiement par son ID de transaction PayDunya
     * @return Le paiement s'il existe
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PendingPayment p WHERE p.transactionId = :tid")
    Optional<PendingPayment> findByTransactionIdForUpdate(@Param("tid") String tid);
}
