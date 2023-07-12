package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Crediter;
import sn.estm.managingrestauranttickets.repositories.CrediterRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CrediterService;

@Service
@Slf4j
public class CrediterServiceImpl implements CrediterService {
    
    @Autowired
    CrediterRepository creditRepository;

    @Override
    public List<Crediter> getAllCredits() {

        return creditRepository.findAll();
    }

    @Override
    public void createCredit(Crediter credit) {
        
        creditRepository.save(credit);
        log.info("added object {}", credit);
        }

    @Override
    public Crediter getCreditById(Long idCredit) {
       Optional<Crediter> optional = creditRepository.findById(idCredit);
	   Crediter credit = null;
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
    public void updateCredit(Long idCredit, Crediter newCredit) {
          
		Crediter credit = this.getCreditById(idCredit);
        if(credit!=null) {
            credit.setIdCredit(newCredit.getIdCredit());
            credit.setDateCredit(newCredit.getDateCredit());
            credit.setCompte(newCredit.getCompte());
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
