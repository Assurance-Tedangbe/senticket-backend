package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Debiter;
import sn.estm.managingrestauranttickets.repositories.DebiterRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebiterService;

@Service
@Slf4j
public class DebiterServiceImpl implements DebiterService{
    
    @Autowired
    DebiterRepository debitRepository;
    
    @Override
    public List<Debiter> getAllDebits() {
        return debitRepository.findAll();
    }

    @Override
    public void createDebit(Debiter debit) {
        
        debitRepository.save(debit);
        log.info("added object {}", debit);
        }

    @Override
    public Debiter getDebitById(Long idDebit) {
        
       Optional<Debiter> optional = debitRepository.findById(idDebit);
	   Debiter debit = null;
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
    public void updateDebit(Long idDebit, Debiter newDebit) {
       
        Debiter debit = this.getDebitById(idDebit);
        if(debit!=null) {
            debit.setIdDebit(newDebit.getIdDebit());
            debit.setDateDebit(newDebit.getDateDebit());
            debit.setCompte(newDebit.getCompte());
            debit.setPortier(newDebit.getPortier());
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
