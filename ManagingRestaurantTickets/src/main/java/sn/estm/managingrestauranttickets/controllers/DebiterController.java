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

import sn.estm.managingrestauranttickets.entities.Debiter;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebiterService;

@RestController
@RequestMapping("/api/debits")
public class DebiterController {

    @Autowired
    DebiterService debiterService;

    
    @GetMapping()
    public List<Debiter> getAllDebits() {
        return debiterService.getAllDebits();
    }
    @PostMapping()
    public void createDebit(@RequestBody Debiter debit) {
        debiterService.createDebit(debit);
    }

    @GetMapping("/{idDebit}")
    public Debiter getDebit(@PathVariable("idDebit") Long idDebit) {
        return debiterService.getDebitById(idDebit);
    }
    @PutMapping("/{id}")
    public void updateDebit(@PathVariable("id") Long idDebit,@RequestBody Debiter debit){
        debiterService.updateDebit(idDebit, debit);
    }
    @DeleteMapping("/{id}")
    public void deleteDebit(@PathVariable("id") Long idDebit) {
       debiterService.deleteDebitById(idDebit);
    }
    
}
