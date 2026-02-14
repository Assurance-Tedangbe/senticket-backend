package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import sn.estm.managingrestauranttickets.dto.historydto.PurchaseHistoryDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PurchaseHistoryService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Data
@RestController
@RequestMapping("/api/purchaseHistory")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<PurchaseHistoryDTO>> getAllTransferHistories() {

        List<PurchaseHistoryDTO> purchaseHistories = purchaseHistoryService.readPurchaseHistories();

        log.info("Fetched purchaseHistories: {}", purchaseHistories);

        return new ResponseEntity<>(purchaseHistories, HttpStatus.OK);
    }

    @GetMapping(value = "/dateBetween")
    @ResponseStatus(HttpStatus.OK)
    public List<PurchaseHistoryDTO> findByPurchaseDateBetween(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return purchaseHistoryService.findByDateBetween(beginDate, endDate);
    }

    @GetMapping(value = "/{purchaseHistoryId}", produces = "application/json")
    public ResponseEntity<PurchaseHistoryDTO> getPurchaseHistoryById(@PathVariable Long purchaseHistoryId) {

        log.info("Fetched PurchaseHistory with ID: {}", purchaseHistoryId);

        PurchaseHistoryDTO purchaseHistoryDTO = purchaseHistoryService
                .readPurchaseHistoryByPurchaseHistoryId(purchaseHistoryId);


        return new ResponseEntity<>(purchaseHistoryDTO, HttpStatus.OK);
    }
}
