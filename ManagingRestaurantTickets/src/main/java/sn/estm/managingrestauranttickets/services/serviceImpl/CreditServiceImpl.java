/* CreditServiceImpl class implementing the CreditService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.dto.CreditDTO;
import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.entities.Credit;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.CreditMapper;
import sn.estm.managingrestauranttickets.repositories.AccountRepository;
import sn.estm.managingrestauranttickets.repositories.CreditRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CreditService;

import java.text.MessageFormat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
  
    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;
    private final AccountRepository accountRepository;

    @Override
    public CreditDTO createCredit(CreditDTO creditDTO) {

        log.info("Creating credit with details: {}", creditDTO);

        Credit credit = creditMapper.toEntity(creditDTO);

        Credit savedCredit = creditRepository.save(credit);

        log.info("Credit created successfully with ID: {}", savedCredit.getCreditId());

        return creditMapper.toDto(savedCredit);
    }

    @Override
    public List<CreditDTO> readCredits() {
        List<Credit> credits = creditRepository.findAll();

        log.info("Reading all credits");

        return credits.stream()
                .map(creditMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public CreditDTO readCreditById(Long creditId) {

        log.info("Reading credit by idCredit: {}", creditId);

        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Credit not found with ID: {0}", creditId)));

        return creditMapper.toDto(credit);
    }


    @Override
    public CreditDTO updateCredit(Long creditId, CreditDTO creditDTO) {
        
        log.info("Updating credit with ID: {}", creditId);

        Credit existingCredit = creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Credit not found with ID: {0}", creditId)));

        existingCredit.setCreditAmount(creditDTO.getCreditAmount());
        existingCredit.setCreditDate(creditDTO.getCreditDate());

        Credit updatedCredit = creditRepository.save(existingCredit);

        log.info("Credit updated successfully with ID: {}", updatedCredit.getCreditId());
        
        return creditMapper.toDto(updatedCredit);
    }
   

    @Override
    public void deleteCredit(Long creditId) {

        log.info("Deleting credit with ID: {}", creditId);

        if (!creditRepository.existsById(creditId)) {
            throw new ResourceNotFoundException(MessageFormat.format(
                "Credit not found with ID: {0}", creditId));
        }
        creditRepository.deleteById(creditId);

        log.info("Credit deleted successfully with ID: {}", creditId);
    }
       

    @Override
    public void linkCreditToAccount(Long creditId, Long accountId) {

        log.info("Linking credit with ID: {} to account with ID: {}", creditId, accountId);

        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Credit not found with ID: {0}", creditId))); 
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                     "Account not found with ID: {0}", accountId)));

        credit.setAccount(account);

        creditRepository.save(credit);

        log.info("Credit with ID: {} linked to account with ID: {}", creditId, accountId);
    }


    @Override
    public void unlinkCreditFromAccount(Long creditId, Long accountId) {

        log.info("Unlinking credit with ID: {} from account with ID: {}", creditId, accountId);

        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Credit not found with ID: {0}", creditId)));

         Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Account not found with ID: {0}", accountId)));

        if (!credit.getAccount().equals(account)) {
            throw new IllegalArgumentException(
                "Credit is not longer linked to the specified account.");
        }
        credit.setAccount(null);

        creditRepository.save(credit);

        log.info("Credit with ID: {} unlinked from account with ID: {}", creditId, accountId);
    }
}

