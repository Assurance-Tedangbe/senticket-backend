package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Compte;
import sn.estm.managingrestauranttickets.entities.Etudiant;
import sn.estm.managingrestauranttickets.entities.Ticket;

public interface EtudiantService {
    
        List<Etudiant> getAllEtudiants();

        void createEtudiant(Etudiant  etudiant);

        Etudiant getEtudiantById(Long idEtudiant);

        void updateEtudiant(Long idEtudiant, Etudiant etu);
     
        void deleteEtudiantById(Long idEtudiant);
    
        //  custom methods

       void  acheterTicket(Compte cpt, Ticket ticket);

       void consulterCompte(Long idEtudiant);

       void transfertArgent(Etudiant etudiant, float montant);

       void annulerTransfert(Etudiant etudiant, float montant);


       
}
