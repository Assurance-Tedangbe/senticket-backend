package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Administrateur;

@Repository
public interface AdminRepository extends JpaRepository<Administrateur, Long>{
    
}
