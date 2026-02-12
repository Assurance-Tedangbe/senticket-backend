package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.estm.managingrestauranttickets.dto.historydto.DebitHistoryDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.DebitHistoryService;

import java.util.List;

@Slf4j
@Data
@RestController
@RequestMapping("/api/debitHistory")
@RequiredArgsConstructor
public class DebitHistoryController {

    private final DebitHistoryService debitHistoryService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<DebitHistoryDTO>> getAllDebitHistories() {

        List<DebitHistoryDTO> debitHistories = debitHistoryService .readDebitHistories();

        log.info("Fetched debitHistories: {}", debitHistories);

        return new ResponseEntity<>(debitHistories, HttpStatus.OK);
    }
}
