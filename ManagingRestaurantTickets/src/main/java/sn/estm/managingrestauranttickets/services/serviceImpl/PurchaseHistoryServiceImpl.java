package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.historydto.PurchaseHistoryDTO;
import sn.estm.managingrestauranttickets.entities.PurchaseHistory;
import sn.estm.managingrestauranttickets.mappers.PurchaseHistoryMapper;
import sn.estm.managingrestauranttickets.repositories.PurchaseHistoryRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PurchaseHistoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final PurchaseHistoryMapper purchaseHistoryMapper;


    @Override
    public List<PurchaseHistoryDTO> readPurchaseHistories() {

        List<PurchaseHistory> purchaseHistories = purchaseHistoryRepository.findAll();

        return purchaseHistories.stream()
                .map(purchaseHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
