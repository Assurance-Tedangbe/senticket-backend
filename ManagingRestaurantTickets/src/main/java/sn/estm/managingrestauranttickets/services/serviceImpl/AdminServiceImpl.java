package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.entities.Administrateur;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.AdminService;

@Service
public class AdminServiceImpl implements AdminService{

    @Override
    public List<Administrateur> getAllAdmins() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllAdmins'");
    }

    @Override
    public void createTicket(Administrateur admin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTicket'");
    }

    @Override
    public Administrateur getAdminById(Long idAdmin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAdminById'");
    }

    @Override
    public void updateAdmin(Long idAdmin, Administrateur admin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAdmin'");
    }

    @Override
    public void deleteAdminById(Long idAdmin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAdminById'");
    }
    
}
