package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.TransfertHistoryDTO;

import java.util.List;

public interface TransfertHistoryService {

    void createTransferHistory(TransfertHistoryDTO transfertHistoryDTO);

    List<TransfertHistoryDTO> readTransfertHistories();
}

