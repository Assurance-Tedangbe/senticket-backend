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

import sn.estm.managingrestauranttickets.entities.Compte;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CompteService;

@RestController
@RequestMapping("/api/comptes")
public class CompteController {
    
    @Autowired
    CompteService compteService;

      
    @GetMapping()
    public List<Compte> getAllComptes() {
        return compteService.getAllComptes();
    }
    @PostMapping()
    public void createCompte(@RequestBody Compte compte) {
        compteService.createCompte(compte);
    }

    @GetMapping("/{idCompte}")
    public Compte getCompte(@PathVariable("idCompte") Long idCpt) {
        return compteService.getCompteById(idCpt);
    }
    @PutMapping("/{id}")
    public void updateCompte(@PathVariable("id") Long idCpt,@RequestBody Compte cpt){
       compteService.updateCompte(idCpt, cpt);
    }
    @DeleteMapping("/{id}")
    public void deleteCompte(@PathVariable("id") Long idCpt) {
       compteService.deleteCompteById(idCpt);
    }
}
