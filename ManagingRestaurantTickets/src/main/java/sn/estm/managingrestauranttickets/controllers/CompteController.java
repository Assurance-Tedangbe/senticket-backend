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

import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.AccountService;

@RestController
@RequestMapping("/api/comptes")
public class CompteController {

    
        role: Admin, Etudiant, Agent:   /api/comptes:
        Créditer/recharge compte :             @PostMapping("/{idCpt}/credit")
        Annuler recharge     :                 @PostMapping(/{idCpt})
        Consulter compte(= getcompteById) :    @GetMapping("/{idCpt}")
        crud compte (5 op)
     
    @Autowired
    AccountService accountService;

      
    @GetMapping()
    public List<Account> getAllComptes() {
        return accountService.getAllComptes();
    }
    @PostMapping()
    public void createCompte(@RequestBody Account compte) {
        accountService.createCompte(compte);
    }

    @GetMapping("/{idCompte}")
    public Account getCompte(@PathVariable("idCompte") Long idCpt) {
        return accountService.getCompteById(idCpt);
    }
    @PutMapping("/{id}")
    public void updateCompte(@PathVariable("id") Long idCpt,@RequestBody Account cpt){
       accountService.updateCompte(idCpt, cpt);
    }
    @DeleteMapping("/{id}")
    public void deleteCompte(@PathVariable("id") Long idCpt) {
       accountService.deleteCompteById(idCpt);
    }
}
 */