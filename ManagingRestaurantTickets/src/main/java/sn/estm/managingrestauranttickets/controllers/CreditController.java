/* CreditController class handling all services in CreditService */
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

import sn.estm.managingrestauranttickets.dto.CreditDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CreditService;

import java.util.List;


@Slf4j
@Data
@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")

    public ResponseEntity<CreditDTO> createCredit(@RequestBody CreditDTO creditDTO) {

        log.info("Creating credit with details: {}", creditDTO);

        CreditDTO createdCredit = creditService.createCredit(creditDTO);

        log.info("Credit created successfully with ID: {}", createdCredit.getCreditId());

        return new ResponseEntity<>(createdCredit, HttpStatus.CREATED);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<CreditDTO>> getAllCredits() {

        log.info("Fetched all credits");

        List<CreditDTO> credits = creditService.readCredits();

        return new ResponseEntity<>(credits, HttpStatus.OK);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{creditId}", produces = "application/json")
    public ResponseEntity<CreditDTO> getCreditById(@PathVariable Long creditId) {

        log.info("Fetched credit with ID: {}", creditId);

        CreditDTO creditDTO = creditService.readCreditById(creditId);

        return new ResponseEntity<>(creditDTO, HttpStatus.OK);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{creditId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreditDTO> updateCredit(@PathVariable Long creditId, 
                                                  @RequestBody CreditDTO creditDTO) {

        log.info("Updating credit with ID: {} with details: {}", creditId, creditDTO);

        CreditDTO updatedCredit = creditService.updateCredit(creditId, creditDTO);
       
        log.info("Credit updated successfully with ID: {}", updatedCredit.getCreditId());
       
        return new ResponseEntity<>(updatedCredit, HttpStatus.OK);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCredit(@PathVariable Long creditId) {

        log.info("Deleting credit with ID: {}", creditId);

        creditService.deleteCredit(creditId);

        log.info("Credit deleted successfully with ID: {}", creditId);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/{creditId}/link/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public void linkCreditToAccount(@PathVariable Long creditId, 
                                    @PathVariable Long accountId) {

        log.info("Linking credit ID: {} to account ID: {}", creditId, accountId);

        creditService.linkCreditToAccount(creditId, accountId);

        log.info("Credit ID: {} linked to account ID: {}", creditId, accountId);

    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/{creditId}/unlink/{accountId}")
    public ResponseEntity<Void> unlinkCreditFromAccount(@PathVariable Long creditId, 
                                                        @PathVariable Long accountId) {

        log.info("Unlinking credit ID: {} from account ID: {}", creditId, accountId);

        creditService.unlinkCreditFromAccount(creditId, accountId);

        log.info("Credit ID: {} unlinked from account ID: {}", creditId, accountId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
