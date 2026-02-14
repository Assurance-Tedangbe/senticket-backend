package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.DebitHistoryDTO;


import java.util.List;

public interface DebitHistoryService {

    void createDebitHistory(DebitHistoryDTO debitHistoryDTO);

    List<DebitHistoryDTO> readDebitHistories();

    DebitHistoryDTO readDebitHistoryByDebitHistoryId(Long debitHistoryId);
}
