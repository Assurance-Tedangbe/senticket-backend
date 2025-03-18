package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Credit;
import sn.estm.managingrestauranttickets.repositories.CreditRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CreditService;

@Service
@Slf4j
public class CreditServiceImpl implements CreditService {
    
    @Autowired
    CreditRepository creditRepository;

    @Override
    public List<Credit> getAllCredits() {

        return creditRepository.findAll();
    }

    @Override
    public void createCredit(Credit credit) {
        
        creditRepository.save(credit);
        log.info("added object {}", credit);
        }

    @Override
    public Credit getCreditById(Long idCredit) {
       Optional<Credit> optional = creditRepository.findById(idCredit);
	   Credit credit = null;
		if(optional.isPresent())
		{
			credit = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idCredit);
		}
        return credit;  
        }

    @Override
    public void updateCredit(Long idCredit, Credit newCredit) {
          
		Credit credit = this.getCreditById(idCredit);
        if(credit!=null) {
            credit.setCreditId(newCredit.getCreditId());
            credit.setCreditDate(newCredit.getCreditDate());
            credit.setAccount(newCredit.getAccount());
            credit.setVendeur(newCredit.getVendeur());
            creditRepository.save(credit);
            log.info("returned to postaman the update object {}", credit);
           
        }
        else
		 throw new UnsupportedOperationException("update failed");
    }

    @Override
    public void deleteCreditById(Long idCredit) {
        creditRepository.deleteById(idCredit);
    }
    
}
