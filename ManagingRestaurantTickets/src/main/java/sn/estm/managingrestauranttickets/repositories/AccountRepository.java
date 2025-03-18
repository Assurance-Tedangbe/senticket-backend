package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
   
    // Compte findByNumeroCompte(String numeroCompte);
    
}
