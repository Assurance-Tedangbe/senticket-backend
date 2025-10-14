package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.estm.managingrestauranttickets.entities.ConsulterMenu;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsulterMenuRepository extends JpaRepository<ConsulterMenu, Long>  {
}
