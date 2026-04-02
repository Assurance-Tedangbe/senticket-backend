package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransactionHistoryService;

import java.time.LocalDate;

@Slf4j
@Data
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping()
    public ResponseEntity<TransactionHistoryResponseDTO> getTransactionHistory(
            @RequestParam(value = "transactionType", defaultValue = "ALL") String transactionType,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size){

        log.info("GET transactions history with filters: type={}, start={}, end={},  page={}, size={}\"",
                transactionType, startDate, endDate, page, size);

        TransactionHistoryResponseDTO response = transactionHistoryService
                .getTransactionHistory(transactionType, startDate, endDate, page, size);

        return ResponseEntity.ok(response);
    }

    /// Récupère l'historique des transactions pour un utilisateur spécifique
    @GetMapping("/user")
    public ResponseEntity<TransactionHistoryResponseDTO> getTransactionHistoryForUser(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "transactionType", defaultValue = "ALL") String transactionType,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        log.info("GET transactions history for user ID: {} with filters: type={}, start={}, end={}, page={}, size={}",
                userId, transactionType, startDate, endDate, page, size);

        TransactionHistoryResponseDTO response = transactionHistoryService
                .getTransactionHistoryForUser(userId, transactionType, startDate, endDate, page, size);

        return ResponseEntity.ok(response);
    }
}
