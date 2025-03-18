package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Debit;

public interface DebitService {
    
        List<Debit> getAllDebits();

        void createDebit(Debit debit);

        Debit getDebitById(Long idDebit);

        void updateDebit(Long idDebit, Debit debit);
     
        void deleteDebitById(Long idDebit);
    
}
