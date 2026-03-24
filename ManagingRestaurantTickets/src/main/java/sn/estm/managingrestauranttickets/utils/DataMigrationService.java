/*
package sn.estm.managingrestauranttickets.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.estm.managingrestauranttickets.entities.*;
import sn.estm.managingrestauranttickets.enumerations.TransactionType;
import sn.estm.managingrestauranttickets.repositories.DebitHistoryRepository;
import sn.estm.managingrestauranttickets.repositories.PurchaseHistoryRepository;
import sn.estm.managingrestauranttickets.repositories.TransactionHistoryRepository;
import sn.estm.managingrestauranttickets.repositories.TransfertHistoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataMigrationService {

    private final PurchaseHistoryRepository purchaseRepo;
    private final DebitHistoryRepository debitRepo;
    private final TransfertHistoryRepository transferRepo;
    private final TransactionHistoryRepository  transactionRepo;

    @Transactional
    public void migrateAllData() {
        log.info("Starting data migration to unified TransactionHistory...");

        migratePurchases();
        migrateDebits();
        migrateTransfers();

        log.info("Data migration completed successfully");
    }

    private void migratePurchases() {
        List<PurchaseHistory> purchases = purchaseRepo.findAll();
        log.info("Migrating {} purchase records", purchases.size());

        purchases.forEach(purchase -> {
            Ticket ticket = purchase.getTicket();

            TransactionHistory history = TransactionHistory.builder()
                    .transactionType(TransactionType.PURCHASE)
                    .date(purchase.getPurchaseDate())
                    .ticketsCount(1)
                    .purchaser(purchase.getPurchaseUser())
                    .ticketIds(ticket.getId().toString())
                    .ticketTypes(ticket.getType().name())
                    .build();

            transactionRepo.save(history);
        });
    }

    private void migrateDebits() {
        List<DebitHistory> debits = debitRepo.findAll();
        log.info("Migrating {} debit records", debits.size());

        debits.forEach(debit -> {
            Ticket ticket = debit.getTicket();

            TransactionHistory history = TransactionHistory.builder()
                    .transactionType(TransactionType.DEBIT)
                    .date(debit.getDebitDate())
                    .ticketsCount(1)
                    .porter(debit.getDebitPorter())
                    .student(debit.getDebitStudent())
                    .ticketIds(ticket.getId().toString())
                    .ticketTypes(ticket.getType().name())
                    .build();

            transactionRepo.save(history);
        });
    }

    private void migrateTransfers() {
        List<TransfertHistory> transfers = transferRepo.findAll();
        log.info("Migrating {} transfer records", transfers.size());

        transfers.forEach(transfer -> {
            List<Long> ticketIds = parseTicketIds(transfer.getTicketIdsTransfered());

            TransactionHistory history = TransactionHistory.builder()
                    .transactionType(TransactionType.TRANSFER)
                    .date(transfer.getTransferDate())
                    .ticketsCount(ticketIds.size())
                    .sender(transfer.getSender())
                    .recipient(transfer.getRecipient())
                    .ticketIds(transfer.getTicketIdsTransfered())
                    .transferCanceled(transfer.isCanceled())
                    .build();

            transactionRepo.save(history);
        });
    }

    private List<Long> parseTicketIds(String ticketIdsStr) {
        if (ticketIdsStr == null || ticketIdsStr.isEmpty()) {
            return List.of();
        }
        String cleaned = ticketIdsStr.replace("[", "").replace("]", "");
        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}

*/
