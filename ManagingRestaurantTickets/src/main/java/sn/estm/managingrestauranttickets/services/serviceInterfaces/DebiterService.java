package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Debiter;

public interface DebiterService {
    
        List<Debiter> getAllDebits();

        void createDebit(Debiter debit);

        Debiter getDebitById(Long idDebit);

        void updateDebit(Long idDebit, Debiter debit);
     
        void deleteDebitById(Long idDebit);
    
}
