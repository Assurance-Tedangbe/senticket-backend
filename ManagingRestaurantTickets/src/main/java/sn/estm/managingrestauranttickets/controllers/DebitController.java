/* DebitController class handling all services in DebitService */
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

import sn.estm.managingrestauranttickets.dto.DebitDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebitService;

import java.util.List;


@Slf4j
@Data
@RestController
@RequestMapping("/api/debits")
@RequiredArgsConstructor
public class DebitController {

    private final DebitService debitService;

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<DebitDTO> createDebit(@RequestBody DebitDTO debitDTO) {

        log.info("Creating debit with details: {}", debitDTO);

        DebitDTO createdDebit = debitService.createDebit(debitDTO);

        log.info("Debit created successfully with ID: {}", createdDebit.getDebitId());

        return new ResponseEntity<>(createdDebit, HttpStatus.CREATED);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<DebitDTO>> getAllDebits() {

        log.info("Fetching all debits");

        List<DebitDTO> debits = debitService.readDebits();

        return new ResponseEntity<>(debits, HttpStatus.OK);
    }
  

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<DebitDTO> getDebitById(@PathVariable Long debitId) {

        log.info("Fetched debit with ID: {}", debitId);

        DebitDTO debitDTO = debitService.readDebitById(debitId);

        return new ResponseEntity<>(debitDTO, HttpStatus.OK);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DebitDTO> updateDebit(@PathVariable Long debitId, 
                                                @RequestBody DebitDTO debitDTO) {

        log.info("Updating debit with ID: {} with details: {}", debitId, debitDTO);

        DebitDTO updatedDebit = debitService.updateDebit(debitId, debitDTO);

        log.info("Debit updated successfully with ID: {}", updatedDebit.getDebitId());

        return new ResponseEntity<>(updatedDebit, HttpStatus.OK);
    }
  

    //@PostAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{debitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDebit(@PathVariable Long debitId) {

        log.info("Deleting debit with ID: {}", debitId);

        debitService.deleteDebit(debitId);

        log.info("Debit deleted successfully with ID: {}", debitId);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/{debitId}/link/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public void linkDebitToAccount(@PathVariable Long debitId, 
                                   @PathVariable Long accountId) {

        log.info("Linking debit ID: {} to account ID: {}", debitId, accountId);

        debitService.linkDebitToAccount(debitId, accountId);

        log.info("Successfully linked debit ID: {} to account ID: {}", debitId, accountId);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/{debitId}/unlink/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public void unlinkDebitFromAccount(@PathVariable Long debitId, 
                                       @PathVariable Long accountId) {

        log.info("Unlinking debit ID: {} from account ID: {}", debitId, accountId);

        debitService.unlinkDebitFromAccount(debitId, accountId);

        log.info("Successfully unlinked debit ID: {} from account ID: {}", debitId, accountId);
    }
}