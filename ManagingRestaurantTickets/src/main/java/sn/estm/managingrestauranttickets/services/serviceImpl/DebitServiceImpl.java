/* DebitServiceImpl class implementing the DebitService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.dto.DebitDTO;
import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.entities.Credit;
import sn.estm.managingrestauranttickets.entities.Debit;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.DebitMapper;
import sn.estm.managingrestauranttickets.repositories.AccountRepository;
import sn.estm.managingrestauranttickets.repositories.DebitRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebitService;

import java.text.MessageFormat;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;


@Slf4j
@Service
@RequiredArgsConstructor
public class DebitServiceImpl implements DebitService {
   
    private final DebitRepository debitRepository;
    private final AccountRepository accountRepository;
    private final DebitMapper debitMapper;

    @Override
    public DebitDTO createDebit(DebitDTO debitDTO) {

        log.info("Creating debit with details: {}", debitDTO);

        Debit debit = debitMapper.toEntity(debitDTO);

        Debit savedDebit = debitRepository.save(debit);
      
        log.info("Debit created successfully with ID: {}", savedDebit.getDebitId());
      
        return debitMapper.toDto(savedDebit);
    }


    @Override
    public List<DebitDTO> readDebits() {
        
        log.info("Reading all debits");

        List<Debit> debits = debitRepository.findAll();

        return debits.stream()
                .map(debitMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public DebitDTO readDebitById(Long debitId) {
        log.info("Reading debit by idDebit: {}", debitId);

        Optional<Debit> debitOptional = debitRepository.findById(debitId);

        if (debitOptional.isPresent()) {
            return debitMapper.toDto(debitOptional.get());
        } else {
            throw new ResourceNotFoundException(MessageFormat.format(
                "Debit not found with ID: {0}", debitId));
        }
    }

  
    @Override
    public DebitDTO updateDebit(Long debitId, DebitDTO debitDTO) {

        log.info("Updating debit with ID: {}", debitId);

        Debit existingDebit = debitRepository.findById(debitId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Debit not found with ID: {0}", debitId)));

        existingDebit.setDebitAmount(debitDTO.getDebitAmount());
        existingDebit.setDebitDate(debitDTO.getDebitDate());
        existingDebit.setUser(debitMapper.toEntity(debitDTO).getUser());
        existingDebit.setAccount(debitMapper.toEntity(debitDTO).getAccount());

        Debit updatedDebit = debitRepository.save(existingDebit);

        log.info("Debit updated successfully with ID: {}", updatedDebit.getDebitId());

        return debitMapper.toDto(updatedDebit);
    }


    @Override
    public void deleteDebit(Long debitId) {

        log.info("Deleting debit with ID: {}", debitId);

        if (!debitRepository.existsById(debitId)) {
            throw new ResourceNotFoundException(MessageFormat.format(
                "Debit not found with ID: {0}", debitId));
        }
        debitRepository.deleteById(debitId);

        log.info("Debit deleted successfully with ID: {}", debitId);
    }


    @Override
    public void linkDebitToAccount(Long debitId, Long accountId) {

        log.info("Linking debit ID: {} to account ID: {}", debitId, accountId);

        Debit debit = debitRepository.findById(debitId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Debit not found with ID: {0}", debitId)));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Account not found with ID: {0}", accountId)));

        debit.setAccount(account);

        debitRepository.save(debit);

        log.info("Debit ID: {} linked to account ID: {}", debitId, accountId);
    }


    @Override
    public void unlinkDebitFromAccount(Long debitId, Long accountId) {

        log.info("Unlinking debit ID: {} from account ID: {}", debitId, accountId);
        
        Debit debit = debitRepository.findById(debitId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Debit not found with ID: {0}", debitId)));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Account not found with ID: {0}", accountId)));

        if (debit.getAccount() != null && debit.getAccount().getAccountId().equals(accountId)) {

            debit.setAccount(null);

            debitRepository.save(debit);

            log.info("Debit ID: {} unlinked from account ID: {}", debitId, accountId);

        } else {
            throw new ResourceNotFoundException(MessageFormat.format(
                "Debit ID: {0} is not linked to account ID: {1}", debitId, accountId));
        }
    }
}
