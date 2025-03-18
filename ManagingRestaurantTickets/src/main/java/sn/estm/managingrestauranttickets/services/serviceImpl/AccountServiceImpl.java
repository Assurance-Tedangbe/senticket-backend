package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.repositories.AccountRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.AccountService;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository compteRepository;
    @Override
    public List<Account> getAllComptes() {
        return compteRepository.findAll();
    }

    @Override
    public void createCompte(Account cpt) {
        compteRepository.save(cpt);
    }

    @Override
    public Account getCompteById(Long idCpt) {
        
        Optional<Account> optional = compteRepository.findById(idCpt);
	    Account compte = null;
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
    public void updateCompte(Long idCpt, Account newCpt) {
      Account cpt = this.getCompteById(idCpt);
        
        if(cpt==null) 
        throw new UnsupportedOperationException("update failed");        
        else{
          cpt.setAccountId(newCpt.getAccountId());
          cpt.setAccountNumber(newCpt.getAccountNumber());
          cpt.setBalance(newCpt.getBalance());
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
    public void activerCompte(Long idCompte, Account compte) {
    }

    @Override
    public void desactiverCompte(Long idCompte, Account compte) {
    }

    @Override
    public void crediterCompte(Account compte, Double addedAmount, Long idCpt) {
        Account cpt = this.getCompteById(idCpt);
        Double newAccount;
        Double solde;
        if(cpt==null)
         throw new UnsupportedOperationException("operation failed");        
        else{
        solde = cpt.getBalance();
        newAccount = solde+addedAmount;
        compte.setBalance(newAccount);
        }
        /*  formulaire operation depot comportant
         montant deposé,frais(OF),statut(effectué),nom de l'agent(facultatif),date et heure,
          nouveau solde,ID transaction(lettres/digits) */
    }

     @Override
    public void annulerRecharge(Account compte, Double addedAmount, Long idCpt) {
    
        Account cpt = this.getCompteById(idCpt);
        Double oldAccount;
        Double solde;
        if(cpt==null)
         throw new UnsupportedOperationException("operation failed");        
        else{
        solde = cpt.getBalance();
        oldAccount = solde-addedAmount;
        compte.setBalance(oldAccount);
        }
         /*  formulaire operation annuler comportant
         montant ,frais(OF),statut(annulé),date et heure, nouveau solde, ID transaction */
    }

    @Override
    public void debiterCompte(Long idCompte, Double amount, Account compte) {
    
        
    /* formulaire operation retrait comportant
       montant retiré,frais(OF),statut(effectué),nom de l'agent(facultatif),date et heure, 
       nouveau solde, ID transaction */
    }

    /* formulaire operation paiement(achat)
     montant,statut(effectué),date et heure,nouveau solde; ID transaction */

     /* formulaire operation transfert
     montant reçu,frais(somme),statut(effectué),date et heure, nouveau solde, ID transaction */ 


}
