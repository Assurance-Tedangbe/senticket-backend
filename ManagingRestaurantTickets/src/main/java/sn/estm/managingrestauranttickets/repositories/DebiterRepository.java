package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Debiter;

@Repository
public interface DebiterRepository extends JpaRepository<Debiter, Long>{
    
}
