package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Administrateur;
import sn.estm.managingrestauranttickets.repositories.AdminRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.AdminService;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService{
    
    @Autowired
    AdminRepository adminRepository;

    @Override
    public List<Administrateur> getAllAdmins() {
      return  adminRepository.findAll();

    }

    @Override
    public void createAdmin(Administrateur admin) {
          adminRepository.save(admin);    }

    @Override
    public Administrateur getAdminById(Long idAdmin) {
        
     Optional<Administrateur> optional = adminRepository.findById(idAdmin);
	   Administrateur admin = null;
		if(optional.isPresent())
		{
			admin = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idAdmin);
		}
		   return admin;
    }

    @Override
    public void updateAdmin(Long idAdmin, Administrateur newAdmin) {
        Administrateur admin = this.getAdminById(idAdmin);
        
        if(admin==null) 
        throw new UnsupportedOperationException("Unimplemented method 'updateTicket'");
        
        else{
          admin.setIdAdmin(newAdmin.getIdAdmin());
          admin.setPrenomAdmin(newAdmin.getPrenomAdmin());
          admin.setNomAdmin(newAdmin.getNomAdmin());
          log.info("returned to postaman the update object {}", admin);
        }
    }

    @Override
    public void deleteAdminById(Long idAdmin) {
       adminRepository.deleteById(idAdmin);
        throw new UnsupportedOperationException("Unimplemented method 'deleteAdminById'");
    }
    
}
