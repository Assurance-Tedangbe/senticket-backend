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

import sn.estm.managingrestauranttickets.entities.Crediter;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CrediterService;

@RestController
@RequestMapping("/api/credits")
public class CrediterController {

    @Autowired
    CrediterService crediterService;

    
    @GetMapping()
    public List<Crediter> getAllCredits() {
        return crediterService.getAllCredits();
    }
    @PostMapping()
    public void addCredit(@RequestBody Crediter credit) {
        crediterService.createCredit(credit);
    }

    @GetMapping("/{idCredit}")
    public Crediter getCredit(@PathVariable("idCredit") Long idCredit) {
        return crediterService.getCreditById(idCredit);
    }
    @PutMapping("/{id}")
    public void updateEleve(@PathVariable("id") Long idCredit,@RequestBody Crediter credit){
        crediterService.updateCredit(idCredit, credit);
    }
    @DeleteMapping("/{id}")
    public void deleteEleve(@PathVariable("id") Long idCredit) {
       crediterService.deleteCreditById(idCredit);
    }
    
}
