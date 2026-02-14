package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.estm.managingrestauranttickets.dto.historydto.TransfertHistoryDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransfertHistoryService;

import java.util.List;

@Slf4j
@Data
@RestController
@RequestMapping("/api/transferHistory")
@RequiredArgsConstructor
public class TransfertHistoryController {

    private final TransfertHistoryService transfertHistoryService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TransfertHistoryDTO>> getAllTransferHistories() {

        List<TransfertHistoryDTO> transferHistories = transfertHistoryService.readTransfertHistories();

        log.info("Fetched transferHistories: {}", transferHistories);

        return new ResponseEntity<>(transferHistories, HttpStatus.OK);
    }

    @GetMapping(value = "/{transferHistoryId}", produces = "application/json")
    public ResponseEntity<TransfertHistoryDTO> getTransferHistoryById(@PathVariable Long transferHistoryId) {

        log.info("Fetched TransferHistory with ID: {}", transferHistoryId);

        TransfertHistoryDTO transfertHistoryDTO = transfertHistoryService.
                readTransferHistoryByTransferHistoryId(transferHistoryId);


        return new ResponseEntity<>(transfertHistoryDTO, HttpStatus.OK);
    }
}
