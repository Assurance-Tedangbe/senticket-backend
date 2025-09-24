/* package sn.estm.managingrestauranttickets.controllers;

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

import sn.estm.managingrestauranttickets.entities.Credit;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CreditService;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    
        role Admin, Etudiant,  Agent:    /api/credits:
        Transférer crédit(de compte à compte)   :      @PostMapping("{idCptExp}/compte/{idCptDest}")
        Annuler transfert crédit : @PostMapping("/{creditId}")
     
    @Autowired
    CreditService creditService;

    
    @GetMapping()
    public List<Credit> getAllCredits() {
        return creditService.getAllCredits();
    }
    @PostMapping()
    public void addCredit(@RequestBody Credit credit) {
        creditService.createCredit(credit);
    }

    @GetMapping("/{idCredit}")
    public Credit getCredit(@PathVariable("idCredit") Long idCredit) {
        return creditService.getCreditById(idCredit);
    }
    @PutMapping("/{id}")
    public void updateEleve(@PathVariable("id") Long idCredit,@RequestBody Credit credit){
        creditService.updateCredit(idCredit, credit);
    }
    @DeleteMapping("/{id}")
    public void deleteEleve(@PathVariable("id") Long idCredit) {
       creditService.deleteCreditById(idCredit);
    }
    
}
 */