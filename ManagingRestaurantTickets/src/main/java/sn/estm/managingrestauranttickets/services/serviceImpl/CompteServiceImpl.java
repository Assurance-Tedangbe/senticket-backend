package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.entities.Compte;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CompteService;

@Service
public class CompteServiceImpl implements CompteService{

    @Override
    public List<Compte> getAllComptes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllComptes'");
    }

    @Override
    public void createCompte(Compte cpt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createCompte'");
    }

    @Override
    public Compte getCompteById(Long idCpt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCompteById'");
    }

    @Override
    public void updateCompte(Long idCpt, Compte cpt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCompte'");
    }

    @Override
    public void deleteCompteById(Long idCompte) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteCompteById'");
    }
    
}
