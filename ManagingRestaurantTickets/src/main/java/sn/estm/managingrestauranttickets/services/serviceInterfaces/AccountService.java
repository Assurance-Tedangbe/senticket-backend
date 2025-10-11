package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.dto.AccountDTO;


public interface AccountService {
    
    AccountDTO createAccount(AccountDTO accountDto);

    List<AccountDTO> readAccounts();

    AccountDTO updateAccount(AccountDTO accountDto);

    void deleteAccount(Long accountId);

    AccountDTO readAccountById(Long accountId);

    void linkAccountToUser(Long accountId, Long userId);

    void unlinkAccountFromUser(Long accountId, Long userId);

    void updateBalance(Long accountId, Double newBalance);

    void updateAccountNumber(Long accountId, String newAccountNumber);

    void activateAccount(Long accountId);

    void deactivateAccount(Long accountId);

}
