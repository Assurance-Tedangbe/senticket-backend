package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Compte;

public interface CompteService {

    List<Compte> getAllComptes();

    void createCompte(Compte cpt);

    Compte getCompteById(Long idCpt);

    void updateCompte(Long idCpt, Compte cpt);
    
    void deleteCompteById(Long idCompte);
 
    //  custom methods
 
     void crediterCompte(Compte compte, Double amount, Long idCpt);

     void annulerRecharge(Compte compte, Double amount, Long idCpt);
   
     void debiterCompte(Long idCompte, Double amount, Compte compte);

     void  activerCompte(Long idCompte, Compte compte);

     void  desactiverCompte(Long idCompte, Compte compte);
     
}
