package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.TransfertHistoryDTO;

import java.util.List;

public interface TransfertHistoryService {

    List<TransfertHistoryDTO> readTransfertHistories();

    TransfertHistoryDTO  readTransferHistoryByTransferHistoryId(Long transferHistoryId);

    List<Long> getTransferHistoryTicketIdsTransfered(Long id);
}

