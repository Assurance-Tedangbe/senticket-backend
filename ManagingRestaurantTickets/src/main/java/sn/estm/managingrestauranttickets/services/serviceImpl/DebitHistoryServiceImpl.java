package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.historydto.DebitHistoryDTO;
import sn.estm.managingrestauranttickets.entities.DebitHistory;
import sn.estm.managingrestauranttickets.entities.PurchaseHistory;
import sn.estm.managingrestauranttickets.entities.TransfertHistory;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.DebitHistoryMapper;
import sn.estm.managingrestauranttickets.mappers.TransfertHistoryMapper;
import sn.estm.managingrestauranttickets.repositories.DebitHistoryRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebitHistoryService;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebitHistoryServiceImpl implements DebitHistoryService {

    private final DebitHistoryRepository debitHistoryRepository;
    private final DebitHistoryMapper debitHistoryMapper;

    @Override
    public void createDebitHistory(DebitHistoryDTO debitHistoryDTO) {
    }

    @Override
    public List<DebitHistoryDTO> readDebitHistories() {

        List<DebitHistory> debitHistories = debitHistoryRepository.findAll();

        return debitHistories.stream()
                .map(debitHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DebitHistoryDTO readDebitHistoryByDebitHistoryId(Long debitHistoryId) {

        log.info("Reading DebitHistory by Id: {}", debitHistoryId);

        DebitHistory debitHistory = debitHistoryRepository .findById(debitHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "DebitHistory not found with ID: {0}", debitHistoryId)));

        return debitHistoryMapper.toDto(debitHistory);
    }
}
