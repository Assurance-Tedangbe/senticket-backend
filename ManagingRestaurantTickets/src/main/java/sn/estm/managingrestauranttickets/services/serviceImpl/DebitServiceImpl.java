/* package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Debit;
import sn.estm.managingrestauranttickets.repositories.DebitRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebitService;

@Service
@Slf4j
public class DebitServiceImpl implements DebitService {
    
    @Autowired
    DebitRepository debitRepository;
    
    @Override
    public List<Debit> getAllDebits() {
        return debitRepository.findAll();
    }

    @Override
    public void createDebit(Debit debit) {
        
        debitRepository.save(debit);
        log.info("added object {}", debit);
        }

    @Override
    public Debit getDebitById(Long idDebit) {
        
       Optional<Debit> optional = debitRepository.findById(idDebit);
	   Debit debit = null;
		if(optional.isPresent())
		{
			debit = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idDebit);
		}
		   return debit;
	    }

    @Override
    public void updateDebit(Long idDebit, Debit newDebit) {
       
        Debit debit = this.getDebitById(idDebit);
        if(debit!=null) {
            debit.setDebitId(newDebit.getDebitId());
            debit.setDebitDate(newDebit.getDebitDate());
            debit.setAccount(newDebit.getAccount());
            debitRepository.save(debit);
            log.info("returned to postaman the update object {}", debit);
            
        }
        else
		 throw new UnsupportedOperationException("update failed");
    }

    @Override
    public void deleteDebitById(Long idDebit) {
        debitRepository.deleteById(idDebit);
    }
    
}
 */