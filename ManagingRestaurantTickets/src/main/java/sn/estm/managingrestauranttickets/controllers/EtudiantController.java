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

import sn.estm.managingrestauranttickets.entities.Etudiant;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.EtudiantService;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    @Autowired
    EtudiantService etudiantService;

    
    @GetMapping()
    public List<Etudiant> getAllEtudiants() {
        return etudiantService.getAllEtudiants();
    }
    @PostMapping()
    public void createEtudiant(@RequestBody Etudiant etu) {
        etudiantService.createEtudiant(etu);;
    }

    @GetMapping("/{idEtudiant}")
    public Etudiant getEtudiant(@PathVariable("idEtudiant") Long idEtu) {
        return etudiantService.getEtudiantById(idEtu);
    }
    @PutMapping("/{id}")
    public void updateEleve(@PathVariable("id") Long idEtudiant,@RequestBody Etudiant etu){
        etudiantService.updateEtudiant(idEtudiant, etu);
    }
    @DeleteMapping("/{id}")
    public void deleteEtudiant(@PathVariable("id") Long idEtu) {
      etudiantService.deleteEtudiantById(idEtu);
    }

    
}
