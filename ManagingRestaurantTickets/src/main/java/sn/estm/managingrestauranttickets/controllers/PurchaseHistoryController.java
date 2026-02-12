package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.estm.managingrestauranttickets.dto.historydto.PurchaseHistoryDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.PurchaseHistoryService;

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
}
