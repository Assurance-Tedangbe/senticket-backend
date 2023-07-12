package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.entities.Crediter;

public interface CrediterService {

        List<Crediter> getAllCredits();

        void createCredit(Crediter credit);

        Crediter getCreditById(Long idCredit);

        void updateCredit(Long idCredit, Crediter credit);
     
        void deleteCreditById(Long idCredit);
    
}
