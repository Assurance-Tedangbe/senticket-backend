package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Compte;
import sn.estm.managingrestauranttickets.entities.Etudiant;
import sn.estm.managingrestauranttickets.entities.Ticket;

public interface EtudiantService {
    
        List<Etudiant> getAllEtudiants();

        void createEtudiant(Etudiant  etu);

        Etudiant getEtudiantById(Long idEtudiant);

        void updateEtudiant(Long idEtudiant, Etudiant etu);
     
        void deleteEtudiantById(Long idEtudiant);
    
        //  custom methods
       void  activerCompte(Long idEtudiant, Compte compte);

       void  acheterTicket(Compte cpt, Ticket ticket);

       Etudiant consulterCompte(Long idEtudiant);

       void transfertArggent()
}
