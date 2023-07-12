package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Etudiant;

public interface EtudiantService {
    
        List<Etudiant> getAllEtudiants();

        void createEtudiant(Etudiant  etu);

        Etudiant getEtudiantById(Long idEtudiant);

        void updateEtudiant(Long idEtudiant, Etudiant etu);
     
        void deleteEtudiantById(Long idEtudiant);
}
