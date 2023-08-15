package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Portier;
import sn.estm.managingrestauranttickets.repositories.PortierRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PortierService;

@Service
@Slf4j
public class PortierServiceImpl implements PortierService{
    @Autowired
    PortierRepository portierRepository;

    @Override
    public List<Portier> getAllPortiers() {
       return portierRepository.findAll();
    }

    @Override
    public void createPortier(Portier portier) {
        portierRepository.save(portier);
        log.info("added object {}", portier);
    }

    @Override
    public Portier getPortierById(Long idPortier) {
        
        Optional<Portier> optional = portierRepository.findById(idPortier);
	    Portier portier = null;
		if(optional.isPresent())
		{
			portier = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idPortier);
		}
		   return portier;
    }

    @Override
    public void updatePortier(Long idPortier, Portier newPortier) {
        Portier portier = this.getPortierById(idPortier);
        if(portier!=null) {
            portier.setIdPortier(newPortier.getIdPortier());
            portier.setNomPortier(newPortier.getNomPortier());
            portier.setPrenomPortier(newPortier.getPrenomPortier());
            portier.setTelPortier(newPortier.getTelPortier());
            portier.setSexePortier(newPortier.getSexePortier());
            portier.setListDebits(newPortier.getListDebits());
            portier.setEtudiant(newPortier.getEtudiant());
            portierRepository.save(portier);
          
            log.info("returned to postaman the update object {}", portier);
        }
        else
		 throw new UnsupportedOperationException("update failed");
         }

    @Override
    public void deletePortierById(Long idPortier) {
        portierRepository.deleteById(idPortier);
    }

    @Override
    public void scannerCode() {
    }
 
}
