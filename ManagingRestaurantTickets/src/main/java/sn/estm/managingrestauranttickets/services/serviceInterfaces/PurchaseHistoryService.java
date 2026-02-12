package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.PurchaseHistoryDTO;

import java.util.List;

public interface PurchaseHistoryService {

    List<PurchaseHistoryDTO> readPurchaseHistories();
}
