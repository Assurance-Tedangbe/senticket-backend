package sn.estm.managingrestauranttickets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.estm.managingrestauranttickets.entities.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    /**
     * Retrieves an Optional containing the Account entity with the specified account number.
     * @param accountNumber the unique identifier of the account to find
     * @return an Optional containing the Account if found, or an empty Optional if not found
     */
    Account findByAccountNumber(String accountNumber);

    /**
    * Checks if an account exists with the specified account number.
    * @param accountNumber the account number to check for existence
    * @return true if an account with the given account number exists, false otherwise
    */
    boolean existsByAccountNumber(String accountNumber);

    /**
    * Checks if all accounts exist for the specified user ID.
    * @param userId the user ID to check associated accounts for
    * @return true if all accounts exist for the given user ID, false otherwise
    */
    boolean existsAllByUserUserId(Long userId);

    /**
    * Retrieves the account associated with the specified user ID.
    * @param userId the user ID whose account is to be retrieved
    * @return the Account entity associated with the given user ID, or null if not found
    */
    Account findByUserUserId(Long userId);  

}
