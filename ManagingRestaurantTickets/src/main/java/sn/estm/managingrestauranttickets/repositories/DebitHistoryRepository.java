package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.entities.DebitHistory;

@Repository
public interface DebitHistoryRepository extends JpaRepository<DebitHistory, Long>{

}
