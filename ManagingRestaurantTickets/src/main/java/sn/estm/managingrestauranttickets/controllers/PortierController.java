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

import sn.estm.managingrestauranttickets.entities.Portier;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PortierService;

@RestController
@RequestMapping("/api/portiers")
public class PortierController {

    @Autowired
    PortierService portierService;

    
    @GetMapping()
    public List<Portier> getAllPortiers() {
        return portierService.getAllPortiers();
    }
    @PostMapping()
    public void addPortier(@RequestBody Portier portier) {
        portierService.createPortier(portier);
    }

    @GetMapping("/{idPortier}")
    public Portier getPortier(@PathVariable("idPortier") Long idPortier) {
        return portierService.getPortierById(idPortier);
    }
    @PutMapping("/{id}")
    public void updatePortier(@PathVariable("id") Long idPortier,@RequestBody Portier portier){
       portierService.updatePortier(idPortier, portier);
    }
    @DeleteMapping("/{id}")
    public void deletePortier(@PathVariable("id") Long idPortier) {
       portierService.deletePortierById(idPortier);
    }
    
}
