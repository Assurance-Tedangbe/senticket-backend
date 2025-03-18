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

import sn.estm.managingrestauranttickets.entities.Debit;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebitService;

@RestController
@RequestMapping("/api/debits")
public class DebiterController {

    /*
        role Admin, Portier:
        /api/debits:
        Débiter compte : @PostMapping("{idCpt}/compte/")
        Scanner code/vérifier identité étudiant :  @GetMapping("/code")
     */
    @Autowired
    DebitService debitService;

    
    @GetMapping()
    public List<Debit> getAllDebits() {
        return debitService.getAllDebits();
    }
    @PostMapping()
    public void createDebit(@RequestBody Debit debit) {
        debitService.createDebit(debit);
    }

    @GetMapping("/{idDebit}")
    public Debit getDebit(@PathVariable("idDebit") Long idDebit) {
        return debitService.getDebitById(idDebit);
    }
    @PutMapping("/{id}")
    public void updateDebit(@PathVariable("id") Long idDebit,@RequestBody Debit debit){
        debitService.updateDebit(idDebit, debit);
    }
    @DeleteMapping("/{id}")
    public void deleteDebit(@PathVariable("id") Long idDebit) {
       debitService.deleteDebitById(idDebit);
    }
    
}
