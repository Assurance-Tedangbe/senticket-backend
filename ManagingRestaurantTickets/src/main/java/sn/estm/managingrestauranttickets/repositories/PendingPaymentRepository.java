// Interface pour les opérations CRUD sur les paiements temporaires

package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.entities.PendingPayment;

import java.util.Optional;

@Repository
public interface PendingPaymentRepository extends JpaRepository<PendingPayment, Long> {

    /**
     * Recherche un paiement par son ID de transaction PayDunya
     * @param transactionId L'identifiant de transaction PayDunya
     * @return Le paiement s'il existe
     */
    Optional<PendingPayment> findByTransactionId(String transactionId);
}