package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Debit;

@Repository
public interface DebitRepository extends JpaRepository<Debit, Long>{
    
}
