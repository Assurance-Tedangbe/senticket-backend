package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Compte;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long>{
   
    // Compte findByNumeroCompte(String numeroCompte);
    
}
