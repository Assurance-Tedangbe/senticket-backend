package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Administrateur;

public interface AdminService {

        List<Administrateur> getAllAdmins();

        void createTicket(Administrateur admin);

        Administrateur getAdminById(Long idAdmin);

        void updateAdmin(Long idAdmin, Administrateur admin);
     
        void deleteAdminById(Long idAdmin);
    
}
