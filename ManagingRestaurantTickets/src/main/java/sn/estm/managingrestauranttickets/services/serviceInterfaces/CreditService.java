package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.dto.CreditDTO;


public interface CreditService {

        CreditDTO createCredit(CreditDTO creditDTO);
        
        List<CreditDTO> readCredits();

        CreditDTO readCreditById(Long idCredit);

        CreditDTO updateCredit(Long idCredit, CreditDTO creditDTO);

        void deleteCredit(Long idCredit);

        void linkCreditToAccount(Long creditId, Long accountId);

        void unlinkCreditFromAccount(Long creditId, Long accountId);
     
}
