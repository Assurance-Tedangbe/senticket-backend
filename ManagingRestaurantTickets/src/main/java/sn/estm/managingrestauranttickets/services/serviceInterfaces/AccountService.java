package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Account;

public interface AccountService {

    List<Account> getAllComptes();

    void createCompte(Account cpt);

    Account getCompteById(Long idCpt);

    void updateCompte(Long idCpt, Account cpt);
    
    void deleteCompteById(Long idCompte);
 
    //  custom methods
 
     void crediterCompte(Account compte, Double amount, Long idCpt);

     void annulerRecharge(Account compte, Double amount, Long idCpt);
   
     void debiterCompte(Long idCompte, Double amount, Account compte);

     void  activerCompte(Long idCompte, Account compte);

     void  desactiverCompte(Long idCompte, Account compte);
     
}
