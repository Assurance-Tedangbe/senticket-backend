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

import sn.estm.managingrestauranttickets.entities.Administrateur;
import sn.estm.managingrestauranttickets.entities.Compte;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.AdminService;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    AdminService adminService;
    
     @GetMapping()
    public List<Administrateur> getAllComptes() {
        return adminService.getAllAdmins();
    }
    @PostMapping()
    public void createAdmin(@RequestBody Administrateur admin) {
        adminService.createAdmin(admin);
    }

    @GetMapping("/{idCompte}")
    public Administrateur getAdmin(@PathVariable("idAdmin") Long idAdmin) {
        return adminService.getAdminById(idAdmin);
    }
    @PutMapping("/{id}")
    public void updateAmin(@PathVariable("id") Long idCpt,@RequestBody Administrateur admin){
       adminService.updateAdmin(idCpt, admin);
    }
    @DeleteMapping("/{id}")
    public void deleteAdmin(@PathVariable("id") Long idAdmin) {
       adminService.deleteAdminById(idAdmin);
    }
}
