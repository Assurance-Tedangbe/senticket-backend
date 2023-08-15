package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;
import sn.estm.managingrestauranttickets.entities.Portier;

public interface PortierService {
    
    List<Portier> getAllPortiers();

    void createPortier(Portier portier);

    Portier getPortierById(Long idPortier);

    void updatePortier(Long idPortier, Portier portier);
    
    void deletePortierById(Long idPortier);

     void scannerCode();
}
