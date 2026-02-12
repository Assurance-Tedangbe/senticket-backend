package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.entities.TransfertHistory;

@Repository
public interface TransfertHistoryRepository extends JpaRepository<TransfertHistory, Long>{

}
