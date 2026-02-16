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
     /**
     * Retrieves an Optional containing the User entity with the specified username.
     * @param username the unique identifier of the user to find
     * @return an Optional containing the User if found, or an empty Optional if not found
     */
    /* Returns an Optional<User> to safely handle cases where a user
     with the given username may not exist, avoiding null pointer exceptions. */
    Optional<User> findByUsername(String username);

    /**
     * Retrieves an Optional containing the User entity with the specified userId.
     * @param id the unique identifier of the user to find
     * @return an Optional containing the User if found, or an empty Optional if not found
     */
    Optional<User> findById(Long id);

    /**
     * Checks if a user with the specified username exists in the repository.
     * @param username the username to check for existence
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if there exists at least one user with the specified role ID.
     * @param id the ID of the role to check for
     * @return {@code true} if at least one user exists with the given role ID, {@code false} otherwise
     */
    boolean existsAllByRoleId(Long id);
}

