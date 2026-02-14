package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.historydto.TransfertHistoryDTO;
import sn.estm.managingrestauranttickets.entities.TransfertHistory;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.TransfertHistoryMapper;
import sn.estm.managingrestauranttickets.repositories.TransfertHistoryRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransfertHistoryService;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransfertHistoryServiceImpl implements TransfertHistoryService {

    private final TransfertHistoryRepository transfertHistoryRepository;
    private final TransfertHistoryMapper transfertHistoryMapper;

    @Override
    public List<TransfertHistoryDTO> readTransfertHistories() {

        List<TransfertHistory> transfertHistories = transfertHistoryRepository.findAll();

        return transfertHistories.stream()
                .map(transfertHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransfertHistoryDTO readTransferHistoryByTransferHistoryId(Long transferHistoryId) {

        log.info("Reading TransferHistory by Id: {}", transferHistoryId);

        TransfertHistory transfertHistory = transfertHistoryRepository.findById(transferHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "TransferHistory not found with ID: {0}", transferHistoryId)));

        return transfertHistoryMapper.toDto(transfertHistory);
    }

    @Override
    public List<Long> getTransferHistoryTicketIdsTransfered(Long id) {

       TransfertHistory transfertHistory = transfertHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "transferHistory not found with ID: {0}", id)));

        List<Long> ticketIds = Arrays.stream(transfertHistory.getTicketIdsTransfered().replace("[", "").replace("]", "").split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        log.info("result en size {} et en contenu: {}", ticketIds.size(), ticketIds);

        return ticketIds.stream().toList();
    }
}
