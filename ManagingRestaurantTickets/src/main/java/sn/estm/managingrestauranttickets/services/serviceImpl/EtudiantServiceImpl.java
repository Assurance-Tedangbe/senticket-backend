package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Etudiant;
import sn.estm.managingrestauranttickets.repositories.EtudiantRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.EtudiantService;

@Service
@Slf4j
public class EtudiantServiceImpl implements EtudiantService{

    @Autowired
    EtudiantRepository etudiantRepository;

    @Override
    public List<Etudiant> getAllEtudiants() {
     return  etudiantRepository.findAll();
    }

    @Override
    public void createEtudiant(Etudiant etu) {
       etudiantRepository.save(etu);
    }

    @Override
    public Etudiant getEtudiantById(Long idEtudiant) {
        
       Optional<Etudiant> optional = etudiantRepository.findById(idEtudiant);
	   Etudiant etu = null;
		if(optional.isPresent())
		{
			etu = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idEtudiant);
		}
		   return etu;
    }

    @Override
    public void updateEtudiant(Long idEtudiant, Etudiant newEtudiant) {
       Etudiant etudiant = this.getEtudiantById(idEtudiant);
        
        if(etudiant==null) 
        throw new UnsupportedOperationException("update failed");        
        else{
          etudiant.setIdEtudiant(newEtudiant.getIdEtudiant());
          etudiant.setNom(newEtudiant.getNom());
          etudiant.setPrenom(newEtudiant.getPrenom());
          etudiant.setNumeroCarte(newEtudiant.getNumeroCarte());
          etudiant.setTel(newEtudiant.getTel());
          etudiant.setSexeEtud(newEtudiant.getSexeEtud());
          etudiant.setFiliere(newEtudiant.getFiliere());
          etudiant.setCompte(newEtudiant.getCompte());
          etudiant.setListTickets(newEtudiant.getListTickets());
          etudiant.setPortier(newEtudiant.getPortier());
          etudiantRepository.save(etudiant);
          log.info("returned to postaman the update object {}", etudiant);
        }
      
    }

    @Override
    public void deleteEtudiantById(Long idEtudiant) {
        etudiantRepository.deleteById(idEtudiant);
    }
    
}
