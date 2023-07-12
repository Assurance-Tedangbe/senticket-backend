package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.entities.Crediter;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.CrediterService;

@Service
public class CrediterServiceImpl implements CrediterService {

    @Override
    public List<Crediter> getAllCredits() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllCredits'");
    }

    @Override
    public void createCredit(Crediter credit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createCredit'");
    }

    @Override
    public Crediter getCreditById(Long idCredit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCreditById'");
    }

    @Override
    public void updateCredit(Long idCredit, Crediter credit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCredit'");
    }

    @Override
    public void deleteCreditById(Long idCredit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteCreditById'");
    }
    
}
