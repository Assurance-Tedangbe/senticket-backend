package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.historydto.TransfertHistoryDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransfertHistoryService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransfertHistoryServiceImpl implements TransfertHistoryService {

    @Override
    public void createTransferHistory(TransfertHistoryDTO transfertHistoryDTO) {
    }

    @Override
    public List<TransfertHistoryDTO> readTransfertHistories() {
        return List.of();
    }
}
