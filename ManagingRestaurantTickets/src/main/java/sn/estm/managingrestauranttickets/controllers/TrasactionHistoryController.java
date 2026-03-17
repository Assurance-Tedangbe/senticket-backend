package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransactionHistoryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Data
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TrasactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping("/history")
    public ResponseEntity<List<TransactionHistoryResponseDTO>> getTransactionHistory(
            @RequestParam(value = "transactionType", defaultValue = "ALL") String transactionType,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate, //LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        log.info("GET transactions with filters: type={}, start={}, end={}, page={}, size={}",
                transactionType, startDate, endDate, page, size);

        List<TransactionHistoryResponseDTO> response = transactionHistoryService.readTransactionHistory(
                transactionType, startDate, endDate, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

   /* seconde propose
   @GetMapping()
    public ResponseEntity<TransactionHistoryResponseDTO> getTransactionHistory(
            @RequestParam(value = "transactionType", defaultValue = "ALL") String transactionType,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, //LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate, //LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        // ... reste du code
    }*/
}
