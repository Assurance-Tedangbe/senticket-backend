package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;
import sn.estm.managingrestauranttickets.mappers.TransactionHistoryMapper;
import sn.estm.managingrestauranttickets.repositories.TransactionHistoryRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransactionHistoryService;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistoryMapper transactionHistoryMapper;

    @Override
    public List<TransactionHistoryResponseDTO> readTransactionHistory(String transactionType, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        return List.of();
    }
}
