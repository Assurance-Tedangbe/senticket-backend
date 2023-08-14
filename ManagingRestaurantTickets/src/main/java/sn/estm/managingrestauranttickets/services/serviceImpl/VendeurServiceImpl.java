package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Vendeur;
import sn.estm.managingrestauranttickets.repositories.VendeurRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.VendeurService;

@Service
@Slf4j
public class VendeurServiceImpl implements VendeurService{

   @Autowired
   VendeurRepository vendeurRepository;

    @Override
    public List<Vendeur> getAllVendeurs() {

       return vendeurRepository.findAll();
    }

    @Override
    public void createVendeur(Vendeur vendeur) {
       vendeurRepository.save(vendeur);
	   log.info("added object {}", vendeur);
	    }

    @Override
    public Vendeur getVendeurById(Long idVendeur) {

       Optional<Vendeur> optional = vendeurRepository.findById(idVendeur);
	   Vendeur vendeur = null;
		if(optional.isPresent())
		{
			vendeur = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idVendeur);
		}
		   return vendeur;
	    }

    @Override
    public void updateVendeur(Long idVendeur, Vendeur newVendeur) {
        
		Vendeur vendeur = this.getVendeurById(idVendeur);
        if(vendeur!=null) {
            vendeur.setIdVendeur(newVendeur.getIdVendeur());
			vendeur.setPrenomVendeur(newVendeur.getPrenomVendeur());
			vendeur.setNomVendeur(newVendeur.getNomVendeur());
			vendeur.setTelVendeur(newVendeur.getTelVendeur());
			vendeur.setSexeVendeur(newVendeur.getSexeVendeur());
			vendeur.setListCredits(newVendeur.getListCredits());
            vendeurRepository.save(vendeur);
            log.info("returned to postaman the update object {}", vendeur);
        }
        else
		 throw new UnsupportedOperationException("update failed");
         
    }

    @Override
    public void deleteVendeurById(Long idVendeur) {

        vendeurRepository.deleteById(idVendeur);
    }

}
