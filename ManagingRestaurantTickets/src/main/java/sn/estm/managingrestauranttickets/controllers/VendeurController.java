package sn.estm.managingrestauranttickets.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sn.estm.managingrestauranttickets.entities.Vendeur;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.VendeurService;

@RestController
@RequestMapping("/api/vendeurs")
public class VendeurController {

    @Autowired
    VendeurService vendeurService;

    
    @GetMapping()
    public List<Vendeur> getAllVendeurs() {
        return vendeurService.getAllVendeurs();
    }
    @PostMapping()
    public void addEleve(@RequestBody Vendeur newVendeur) {
        vendeurService.createVendeur(newVendeur);
    }

    @GetMapping("/{idVendeur}")
    public Vendeur getVendeur(@PathVariable("idVendeur") Long idVend) {
        return vendeurService.getVendeurById(idVend);
    }
    @PutMapping("/{id}")
    public void updateVendeur(@PathVariable("id") Long idVendeur,@RequestBody  Vendeur vendeur){
        vendeurService.updateVendeur(idVendeur, vendeur);
    }
    @DeleteMapping("/{id}")
    public void deleteEleve(@PathVariable("id") Long idVendeur) {
       vendeurService.deleteVendeurById(idVendeur);
    }
    
}
