/* AccountController class handling all services in AccountServiceImpl. */
package sn.estm.managingrestauranttickets.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//import org.springframework.security.access.prepost.PostAuthorize;

import sn.estm.managingrestauranttickets.dto.AccountDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.AccountService;

import java.util.List;


@Slf4j
@Data
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountService accountService;

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {

        log.info("Creating account with details: {}", accountDTO);
       
        AccountDTO createdAccount = accountService.createAccount(accountDTO);
       
        log.info("Account created successfully with ID: {}", createdAccount.getAccountId());
       
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {

        List<AccountDTO> accounts = accountService.readAccounts();

        log.info("Fetched accounts: {}", accounts);

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{accountId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long accountId, @RequestBody AccountDTO accountDTO) {

        log.info("Updating account with ID: {} with details: {}", accountId, accountDTO);
               
        AccountDTO updatedAccount = accountService.updateAccount(accountDTO);
       
        log.info("Account updated successfully with ID: {}", updatedAccount.getAccountId());
       
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }
 

    //@PostAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long accountId) {

        log.info("Deleting account with ID: {}", accountId);
       
        accountService.deleteAccount(accountId);
       
        log.info("Account deleted successfully with ID: {}", accountId);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{accountId}", produces = "application/json")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long accountId) {

        AccountDTO account = accountService.readAccountById(accountId);
       
        log.info("Fetched account with ID: {}", accountId);

       return new ResponseEntity<>(account, HttpStatus.OK);
    }
   

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{accountId}/link/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void linkAccountToUser(@PathVariable Long accountId, @PathVariable Long userId) {

        log.info("Linking account with ID: {} to user with ID: {}", accountId, userId);
       
        accountService.linkAccountToUser(accountId, userId);
       
        log.info("Account with ID: {} linked to user with ID: {}", accountId, userId);
    }
   

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{accountId}/unlink/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void unlinkAccountFromUser(@PathVariable Long accountId, @PathVariable Long userId) {

        log.info("Unlinking account with ID: {} from user with ID: {}", accountId, userId);

        accountService.unlinkAccountFromUser(accountId, userId);

        log.info("Account with ID: {} unlinked from user with ID: {}", accountId, userId);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/balance/{accountId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updateAccountBalance(@PathVariable Long accountId, @RequestBody Double newBalance) {

        log.info("Updating balance for account with ID: {}", accountId, newBalance);

        accountService.updateBalance(accountId, newBalance);

        log.info("Account balance updated successfully for ID: {}", accountId);
    }
    
    
     //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/accountNumber/{accountId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updateAccountNumber(
        @PathVariable Long accountId, @RequestBody String newAccountNumber) {

        log.info("Updating account number for account with ID: {}", accountId, newAccountNumber);

        accountService.updateAccountNumber(accountId, newAccountNumber);

        log.info("Account number updated successfully for ID: {}", accountId);
    }
    
   
    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/activate/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public void activateAccount(@PathVariable Long accountId) {

        log.info("Activating account with ID: {}", accountId);

        accountService.activateAccount(accountId);

        log.info("Account activated successfully with ID: {}", accountId);
    }
    

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/deactivate/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public void deactivateAccount(@PathVariable Long accountId) {

        log.info("Deactivating account with ID: {}", accountId);

        accountService.deactivateAccount(accountId);

        log.info("Account deactivated successfully with ID: {}", accountId);
    } 

    //@PostAuthorize("hasAuthority('ADMIN', 'ETUDIANT')")
    @PutMapping(value = "/transfer/{fromAccountId}/to/{toAccountId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public void transferFunds(@PathVariable Long fromAccountId, 
                              @PathVariable Long toAccountId, 
                              @PathVariable Double amount) {

        log.info("Transferring {} from account ID: {} to account ID: {}",
         amount, fromAccountId, toAccountId);

        accountService.transferFunds(fromAccountId, toAccountId, amount);

        log.info("Transfer of {} from account ID: {} to account ID: {} completed successfully",
         amount, fromAccountId, toAccountId);
    }

    //@PostAuthorize("hasAuthority('ADMIN', 'ETUDIANT')")
    @PutMapping(value = "/cancelTransfer/{fromAccountId}/to/{toAccountId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public void cancelTransferFunds(@PathVariable Long fromAccountId,
                                   @PathVariable Long toAccountId,
                                   @PathVariable Double amount) {

        log.info("Cancelling transfer of {} from account ID: {} to account ID: {}", 
            amount, fromAccountId, toAccountId);

        accountService.cancelTransferFunds(fromAccountId, toAccountId, amount);

        log.info("Cancelled transfer of {} from account ID: {} to account ID: {} successfully",
            amount, fromAccountId, toAccountId);
    }
}

