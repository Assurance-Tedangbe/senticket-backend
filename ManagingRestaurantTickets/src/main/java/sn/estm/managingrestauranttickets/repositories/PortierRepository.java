package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Portier;

@Repository
public interface PortierRepository extends JpaRepository<Portier, Long> {
    
}
