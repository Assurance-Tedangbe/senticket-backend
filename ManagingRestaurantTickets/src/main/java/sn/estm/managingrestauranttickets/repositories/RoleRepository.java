/**
* Generates the RoleRepository interface corresponding to the Role class,
* considering the necessary CRUD operations and add a method to find 
* roles by their name.
**/
package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /* Returns an Optional<Role> to safely handle cases where a role
     with the given name may not exist, avoiding null pointer exceptions. */
    Optional<Role> findByName(String name);
} 
















/* package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
} */