package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.historydto.PurchaseHistoryDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseHistoryService {

    List<PurchaseHistoryDTO> readPurchaseHistories();

    List<PurchaseHistoryDTO> findByDateBetween(LocalDateTime begin, LocalDateTime end);
}
