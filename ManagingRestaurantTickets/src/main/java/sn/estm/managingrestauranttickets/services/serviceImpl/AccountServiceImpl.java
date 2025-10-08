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
      Account account = accountMapper.toEntity(accountDto);
        Account savedAccount = accountRepository.save(account);
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
        Account existingAccount = accountRepository.findById(accountDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountDto.getAccountId())));
        existingAccount.setAccountNumber(accountDto.getAccountNumber());
        existingAccount.setBalance(accountDto.getBalance());
        existingAccount.setActive(accountDto.isActive());
        Account updatedAccount = accountRepository.save(existingAccount);
        return accountMapper.toDto(updatedAccount);
    }


    @Override
    public void deleteAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountId));
        }
        accountRepository.deleteById(accountId);
    }


    @Override
    public AccountDTO readAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountId)));
        return accountMapper.toDto(account);
    }


    @Override
    public void linkAccountToUser(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("User not found with ID: {0}", userId)));
        account.setUser(user);
        accountRepository.save(account);
    }


    @Override
    public void unlinkAccountFromUser(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("User not found with ID: {0}", userId)));
        if (!account.getUser().equals(user)) {
            throw new IllegalArgumentException("Account is not linked to the specified user.");
        }
        account.setUser(null);
        accountRepository.save(account);
    }


    @Override
    public void updateBalance(Long accountId, Double newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountId)));
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
    

    @Override
    public void updateAccountNumber(Long accountId, String newAccountNumber) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountId)));
        account.setAccountNumber(newAccountNumber);
        accountRepository.save(account);
    }


    @Override
    public void activateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountId)));
        account.setActive(true);
        accountRepository.save(account);
    }


    @Override
    public void deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Account not found with ID: {0}", accountId)));
        account.setActive(false);
        accountRepository.save(account);
    }

}
