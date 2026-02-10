package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.historydto.PurchaseHistoryDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PurchaseHistoryService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {

    @Override
    public void createPurchaseHistory(PurchaseHistoryDTO purchaseHistoryDTO) {
    }

    @Override
    public List<PurchaseHistoryDTO> readPurchaseHistories() {
        return List.of();
    }
}
