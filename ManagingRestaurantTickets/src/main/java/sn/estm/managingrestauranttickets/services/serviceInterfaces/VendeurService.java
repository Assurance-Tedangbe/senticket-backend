package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.entities.Vendeur;

import java.util.List;

public interface VendeurService {

    List<Vendeur> getAllVendeurs();

    void createVendeur(Vendeur vendeur);

    Vendeur getVendeurById(Long idVendeur);

    void updateVendeur(Long idVendeur, Vendeur vendeur);
    
    void deleteVendeurById(Long idVendeur);

}
