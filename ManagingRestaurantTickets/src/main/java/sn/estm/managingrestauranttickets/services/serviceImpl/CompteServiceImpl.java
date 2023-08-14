package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Compte;
import sn.estm.managingrestauranttickets.repositories.CompteRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CompteService;

@Service
@Slf4j
public class CompteServiceImpl implements CompteService{

    @Autowired
    CompteRepository compteRepository;
    @Override
    public List<Compte> getAllComptes() {
        return compteRepository.findAll();
    }

    @Override
    public void createCompte(Compte cpt) {
        compteRepository.save(cpt);
    }

    @Override
    public Compte getCompteById(Long idCpt) {
        
        Optional<Compte> optional = compteRepository.findById(idCpt);
	    Compte compte = null;
		if(optional.isPresent())
		{
			compte = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idCpt);
		}
		   return compte;
    }

    @Override
    public void updateCompte(Long idCpt, Compte newCpt) {
      Compte cpt = this.getCompteById(idCpt);
        
        if(cpt==null) 
        throw new UnsupportedOperationException("update failed");        
        else{
          cpt.setIdCpt(newCpt.getIdCpt());
          cpt.setNumeroCompte(newCpt.getNumeroCompte());
          cpt.setSolde(newCpt.getSolde());
          cpt.setDateCreation(newCpt.getDateCreation());
          cpt.setEtudiant(newCpt.getEtudiant());
          cpt.setListCredits(newCpt.getListCredits());
          cpt.setListDebits(newCpt.getListDebits());
          compteRepository.save(cpt);
          log.info("returned to postaman the update object {}", cpt);
        }
    }

    @Override
    public void deleteCompteById(Long idCompte) {
       compteRepository.deleteById(idCompte);        
    }

    @Override
    public void activerCompte(Long idCompte, Compte compte) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'activerCompte'");
    }

    @Override
    public void desactiverCompte(Long idCompte, Compte compte) {
    }

    @Override
    public void crediterCompte(Compte compte, float amount) {
    }

    @Override
    public void annulerRecharge(Long idCompte) {
    }

    @Override
    public void debiterCompte(Long idCompte) {
    }
    
}
