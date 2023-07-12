package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.entities.Debiter;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebiterService;

@Service
public class DebiterServiceImpl implements DebiterService{

    @Override
    public List<Debiter> getAllDebits() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllDebits'");
    }

    @Override
    public void createDebit(Debiter debit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDebit'");
    }

    @Override
    public Debiter getDebitById(Long idDebit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDebitById'");
    }

    @Override
    public void updateDebit(Long idDebit, Debiter debit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDebit'");
    }

    @Override
    public void deleteDebitById(Long idDebit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteDebitById'");
    }
    
}
