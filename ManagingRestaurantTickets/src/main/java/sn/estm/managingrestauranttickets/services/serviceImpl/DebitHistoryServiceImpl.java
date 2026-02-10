package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.historydto.DebitHistoryDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebitHistoryService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebitHistoryServiceImpl implements DebitHistoryService {

    @Override
    public void createDebitHistory(DebitHistoryDTO debitHistoryDTO) {
    }

    @Override
    public List<DebitHistoryDTO> readDebitHistories() {
        return List.of();
    }
}
