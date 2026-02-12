package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.entities.PurchaseHistory;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long>{

    List<PurchaseHistory> findByPurchaseDateBetween(LocalDateTime beginDate, LocalDateTime endDate);

}
