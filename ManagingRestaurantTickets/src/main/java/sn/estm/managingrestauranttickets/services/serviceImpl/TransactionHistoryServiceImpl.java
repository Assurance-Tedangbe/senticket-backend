package sn.estm.managingrestauranttickets.services.serviceImpl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketType;
import sn.estm.managingrestauranttickets.enumerations.TransactionType;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.TransactionHistoryMapper;
import sn.estm.managingrestauranttickets.repositories.TransactionHistoryRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransactionHistoryService;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistoryMapper transactionHistoryMapper;


    @Override
    public TransactionHistoryResponseDTO getTransactionHistory(
            String transactionType,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        log.info("Fetching transaction history with filters: type={}, start={}, end={}, page={}, size={}",
                transactionType, startDate, endDate, page, size);

        // Définir les dates par défaut si non fournies
        LocalDateTime startDateTime = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDateTime.of(2000, 1, 1, 0, 0); // Date très ancienne par défaut

        LocalDateTime endDateTime = (endDate != null)
                ? endDate.atTime(LocalTime.MAX)
                : LocalDateTime.now(); // Date courante par défaut

        // Créer la pagination avec tri par date décroissante
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Page<TransactionHistory> transactionPage;

        // Si le type est "ALL" ou null, on ne filtre pas par type
        if (transactionType == null || transactionType.equalsIgnoreCase("ALL")) {
            transactionPage = transactionHistoryRepository
                    .findByDateBetweenOrderByDateDesc(startDateTime, endDateTime, pageable);
        } else {
            // Convertir le string en enum TransactionType
            try {
                TransactionType type = TransactionType.valueOf(transactionType.toUpperCase());
                transactionPage = transactionHistoryRepository
                        .findByTransactionTypeAndDateBetween(type, startDateTime, endDateTime, pageable);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid transaction type: {}, returning all transactions", transactionType);
                transactionPage = transactionHistoryRepository
                        .findByDateBetweenOrderByDateDesc(startDateTime, endDateTime, pageable);
            }
        }

        // Convertir les entités en DTOs
        List<TransactionHistoryDTO> content = transactionPage.getContent().stream()
                .map(transactionHistoryMapper::toDto)
                .collect(Collectors.toList());

        // Construire la réponse paginée
        return TransactionHistoryResponseDTO.builder()
                .content(content)
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .currentPage(transactionPage.getNumber())
                .pageSize(transactionPage.getSize())
                .first(transactionPage.isFirst())
                .last(transactionPage.isLast())
                .hasNext(transactionPage.hasNext())
                .hasPrevious(transactionPage.hasPrevious())
                .build();
    }

    /// Méthodes utilitaires pour enregistrer les transactions
    @Transactional
    @Override
    public TransactionHistory recordPurchase(User purchaser, List<Ticket> tickets, double amount) {

        TransactionHistory history = TransactionHistory.builder()
                .transactionType(TransactionType.PURCHASE)
                .ticketsCount(tickets.size())
                .purchaser(purchaser)
                .ticketIds(joinIds(tickets))
                .ticketSummary(summarize(tickets))
                .amount(amount)
                .build();

        log.info("RecordPurchase for user: {}, tickets: {}", purchaser.getUsername(), tickets.size());
        return transactionHistoryRepository.save(history);
    }

    @Transactional
    @Override
    public TransactionHistory recordDebit(User porter, User student, List<Ticket> tickets) {

        double amount = tickets.stream().mapToDouble(Ticket::getPrice).sum();
        TransactionHistory history = TransactionHistory.builder()
                .transactionType(TransactionType.DEBIT)
                .ticketsCount(tickets.size())
                .porter(porter)
                .student(student)
                .ticketIds(joinIds(tickets))
                .ticketSummary(summarize(tickets))
                .amount(amount)
                .build();

        log.info("RecordDebit by porter: {} for student: {}, tickets: {}",
                porter.getUsername(), student.getUsername(), tickets.size());
        return transactionHistoryRepository.save(history);
    }

    @Transactional
    @Override
    public TransactionHistory recordTransfer(User sender, User recipient, List<Ticket> tickets) {

        TransactionHistory history = TransactionHistory.builder()
                .transactionType(TransactionType.TRANSFER)
                .ticketsCount(tickets.size())
                .sender(sender)
                .recipient(recipient)
                .ticketIds(joinIds(tickets))
                .ticketSummary(summarize(tickets))
                .transferCanceled(false)
                .build();

        log.info("RecordTransfer from: {} to: {}, tickets: {}",
                sender.getUsername(), recipient.getUsername(), tickets.size());
        return transactionHistoryRepository.save(history);
    }
    private String joinIds(List<Ticket> tickets) {
        return tickets.stream()
                .map(t -> t.getId().toString())
                .collect(Collectors.joining(","));
    }

    private String summarize(List<Ticket> tickets) {
        long a = tickets.stream().filter(t -> t.getType() == TicketType.A).count();
        long b = tickets.stream().filter(t -> t.getType() == TicketType.B).count();
        // build a summary string like "2×A, 1×B"
        List<String> parts = new ArrayList<>();
        if (a > 0) parts.add(a + "×A");
        if (b > 0) parts.add(b + "×B");
        return String.join(", ", parts);
    }

    /**
     *  Récupère l'historique des transactions pour un utilisateur spécifique
     * Cette méthode filtre les transactions où l'utilisateur est impliqué selon son rôle :
     * - Pour un ÉTUDIANT : transactions où il est purchaser, student, sender ou recipient
     * - Pour un PORTIER : transactions de type DEBIT
     * - Pour un ADMIN : peut voir toutes les transactions
     */
    @Override
    public TransactionHistoryResponseDTO getTransactionHistoryForUser(
            Long userId,
            String transactionType,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        log.info("Fetching transaction history for user ID: {} with filters: type={}, start={}, end={}, page={}, size={}",
                userId, transactionType, startDate, endDate, page, size);

        // Définir les dates par défaut si non fournies
        LocalDateTime startDateTime = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDateTime.of(2000, 1, 1, 0, 0);

        LocalDateTime endDateTime = (endDate != null)
                ? endDate.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        // Créer la pagination avec tri par date décroissante
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Page<TransactionHistory> transactionPage;

        // Si le type est "ALL" ou null, on ne filtre pas par type
        if (transactionType == null || transactionType.equalsIgnoreCase("ALL")) {
            transactionPage = transactionHistoryRepository
                    .findByUserIdAndDateBetween(userId, startDateTime, endDateTime, pageable);
        } else {
            try {
                TransactionType type = TransactionType.valueOf(transactionType.toUpperCase());
                transactionPage = transactionHistoryRepository
                        .findByUserIdAndTransactionTypeAndDateBetween(
                                userId, type, startDateTime, endDateTime, pageable);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid transaction type: {}, returning all transactions for user", transactionType);
                transactionPage = transactionHistoryRepository
                        .findByUserIdAndDateBetween(userId, startDateTime, endDateTime, pageable);
            }
        }

        // Convertir les entités en DTOs
        List<TransactionHistoryDTO> content = transactionPage.getContent().stream()
                .map(transactionHistoryMapper::toDto)
                .collect(Collectors.toList());

        // Construire la réponse paginée
        return TransactionHistoryResponseDTO.builder()
                .content(content)
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .currentPage(transactionPage.getNumber())
                .pageSize(transactionPage.getSize())
                .first(transactionPage.isFirst())
                .last(transactionPage.isLast())
                .hasNext(transactionPage.hasNext())
                .hasPrevious(transactionPage.hasPrevious())
                .build();
    }

    @Override
    public TransactionHistoryDTO readTransactionHistoryById(Long id) {
        log.info("Reading TransactionHistory by Id: {}", id);

        TransactionHistory transactionHistory = transactionHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "TransferHistory not found with ID: {0}", id)));

        return transactionHistoryMapper.toDto(transactionHistory);
    }
}
