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
    }

    @Override
    public void desactiverCompte(Long idCompte, Compte compte) {
    }

    @Override
    public void crediterCompte(Compte compte, Double addedAmount, Long idCpt) {
        Compte cpt = this.getCompteById(idCpt);
        Double newAccount;
        Double solde;
        if(cpt==null)
         throw new UnsupportedOperationException("operation failed");        
        else{
        solde = cpt.getSolde();
        newAccount = solde+addedAmount;
        compte.setSolde(newAccount);
        }
        /*  formulaire operation depot comportant
         montant deposé,frais(OF),statut(effectué),nom de l'agent(facultatif),date et heure,
          nouveau solde,ID transaction(lettres/digits) */
    }

     @Override
    public void annulerRecharge(Compte compte, Double addedAmount, Long idCpt) {
    
        Compte cpt = this.getCompteById(idCpt);
        Double oldAccount;
        Double solde;
        if(cpt==null)
         throw new UnsupportedOperationException("operation failed");        
        else{
        solde = cpt.getSolde();
        oldAccount = solde-addedAmount;
        compte.setSolde(oldAccount);
        }
         /*  formulaire operation annuler comportant
         montant ,frais(OF),statut(annulé),date et heure, nouveau solde, ID transaction */
    }

    @Override
    public void debiterCompte(Long idCompte, Double amount, Compte compte) {
    
        
    /* formulaire operation retrait comportant
       montant retiré,frais(OF),statut(effectué),nom de l'agent(facultatif),date et heure, 
       nouveau solde, ID transaction */
    }

    /* formulaire operation paiement(achat)
     montant,statut(effectué),date et heure,nouveau solde; ID transaction */

     /* formulaire operation transfert
     montant reçu,frais(somme),statut(effectué),date et heure, nouveau solde, ID transaction */ 


}
