/** Interface for managing debit transactions. */
package sn.estm.managingrestauranttickets.services.serviceInterfaces;
import java.util.List;
import sn.estm.managingrestauranttickets.dto.DebitDTO;


public interface DebitService {
        
    DebitDTO createDebit(DebitDTO debitDTO);

    List<DebitDTO> readDebits();

    DebitDTO readDebitById(Long idDebit);

    DebitDTO updateDebit(Long idDebit, DebitDTO debitDTO);

    void deleteDebit(Long idDebit);
    
    void linkDebitToAccount(Long debitId, Long accountId);

    void unlinkDebitFromAccount(Long debitId, Long accountId);    
}
