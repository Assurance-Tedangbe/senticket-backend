package sn.estm.managingrestauranttickets.services.serviceImpl;

import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.AccountDTO;
import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.AccountMapper;
import sn.estm.managingrestauranttickets.repositories.AccountRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.AccountService;
import java.text.MessageFormat;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final AccountMapper accountMapper;

  @Override
  public AccountDTO createAccount(AccountDTO accountDto) {
      log.info("Creating account with details: {}", accountDto);

      Account account = accountMapper.toEntity(accountDto);
      
      Account savedAccount = accountRepository.save(account);

      log.info("Account created successfully with ID: {}", savedAccount.getAccountId());
      return accountMapper.toDto(savedAccount);
    }


    @Override
    public List<AccountDTO> readAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public AccountDTO updateAccount(AccountDTO accountDto) {

        log.info("Updating account details: {}", accountDto);

        Account existingAccount = accountRepository.findById(accountDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountDto.getAccountId())));

        existingAccount.setAccountNumber(accountDto.getAccountNumber());
        existingAccount.setBalance(accountDto.getBalance());
        existingAccount.setDateCreation(accountDto.getDateCreation());
        existingAccount.setActive(accountDto.isActive());

        Account updatedAccount = accountRepository.save(existingAccount);

        log.info("Account updated successfully with account number: {}", updatedAccount.getAccountNumber());
       
        return accountMapper.toDto(updatedAccount);
    }


    @Override
    public void deleteAccount(Long accountId) {
        log.info("Deleting account with accountId: {}", accountId);

        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException(MessageFormat.format(
              "Account not found with ID: {0}", accountId));
        }
        accountRepository.deleteById(accountId);

        log.info("deleteAccount end ok - accountId: {}", accountId);
    }


    @Override
    public AccountDTO readAccountById(Long accountId) {
        log.info("Reading account by accountId: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));

        return accountMapper.toDto(account);
    }


    @Override
    public void linkAccountToUser(Long accountId, Long userId) {
      log.info("Linking account {} to user with userId: {}", accountId, userId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "User not found with ID: {0}", userId)));

       /* if (account.getUser() != null) {
            throw new IllegalArgumentException("Account is already linked to a user.");
        }*/

        /* check if user is already linked to another account */
        /* if (user.getAccount() != null) {
            throw new IllegalArgumentException("User is already linked to another account.");
        } */

        account.setUser(user);

        accountRepository.save(account);

        log.info("Account {} linked to user with userId: {}", accountId, userId);
    }


    @Override
    public void unlinkAccountFromUser(Long accountId, Long userId) {
      log.info("Unlinking account {} from user with userId: {}", accountId, userId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));
                  
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "User not found with ID: {0}", userId)));

        if (!account.getUser().equals(user)) {
            throw new IllegalArgumentException("Account is not longer linked to the specified user.");
        }
        account.setUser(null);

        accountRepository.save(account);
    }


    @Override
    public void updateBalance(Long accountId, Double newBalance) {
        log.info("Updating balance for account with accountId: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));

        account.setBalance(newBalance);

        log.info("Account balance updated successfully for account ID: {}", accountId);

        accountRepository.save(account);
    }


    @Override
    public void updateAccountNumber(Long accountId, String newAccountNumber) {
        log.info("Updating account number for account with accountId: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));

        account.setAccountNumber(newAccountNumber);

        log.info("Account number updated successfully for account ID: {}", accountId);

        accountRepository.save(account);
    }


    @Override
    public void activateAccount(Long accountId) {
        log.info("Activating account with accountId: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));

        account.setActive(true);

        accountRepository.save(account);

        log.info("Account activated successfully for account ID: {}", accountId);
    }


    @Override
    public void deactivateAccount(Long accountId) {
        log.info("Deactivating account with accountId: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));

        account.setActive(false);

        accountRepository.save(account);
        
        log.info("Account deactivated successfully for account ID: {}", accountId);
    }

}
