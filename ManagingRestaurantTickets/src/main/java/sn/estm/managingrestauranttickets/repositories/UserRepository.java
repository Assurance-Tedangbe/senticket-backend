/**
* Generates the UserRepository interface corresponding to the User class,
* considering the necessary CRUD operations and add a method to find 
* users by their username.
**/
package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.estm.managingrestauranttickets.entities.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /* Returns an Optional<User> to safely handle cases where a user
     with the given username may not exist, avoiding null pointer exceptions. */
    Optional<User> findByUsername(String username);
}

