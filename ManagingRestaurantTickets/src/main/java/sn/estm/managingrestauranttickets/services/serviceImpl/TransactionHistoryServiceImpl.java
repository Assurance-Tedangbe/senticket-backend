package sn.estm.managingrestauranttickets.services.serviceImpl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryResponseDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TransactionType;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.repositories.TransactionHistoryRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TransactionHistoryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;

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

       // List<TransactionHistory> transactions;

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

       /* // Convertir les entités en DTOs
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());*/

        // Convertir les entités en DTOs
        List<TransactionHistoryDTO> content = transactionPage.getContent().stream()
                .map(this::convertToDTO)
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

    private TransactionHistoryDTO convertToDTO(TransactionHistory history) {
        switch (history.getTransactionType()) {
            case PURCHASE:
                return convertPurchaseToDTO(history);
                /* // ceci peut remplacer   return convertPurchaseToDTO(history);
                return TransactionHistoryDTO.fromPurchase(
                history.getId(),
                history.getTransactionDate(),
                history.getTicketsCount(),
                convertToUserDTO(history.getUser()),
                parseTicketIds(history.getTicketIds()),
                parseTicketTypes(history.getTicketTypes()),
            );  */
            case DEBIT:
                return convertDebitToDTO(history);
            case TRANSFER:
                return convertTransferToDTO(history);
            default:
                throw new IllegalArgumentException("Unknown transaction type: " + history.getTransactionType());
        }
    }

    private TransactionHistoryDTO convertPurchaseToDTO(TransactionHistory history) {
        return TransactionHistoryDTO.builder()
                .id(history.getId())
                .transactionType(history.getTransactionType())
                .date(history.getDate())
                .ticketsCount(history.getTicketsCount())
                .purchaserDTO(convertToUserDTO(history.getPurchaser()))
                .ticketIds(parseTicketIds(history.getTicketIds()))
                .ticketTypes(parseTicketTypes(history.getTicketTypes()))
                .build();
        /* return TransactionHistoryDTO.fromPurchase(
                history.getId(),
                history.getTransactionDate(),
                history.getTicketsCount(),
                convertToUserDTO(history.getUser()),
                parseTicketIds(history.getTicketIds()),
                parseTicketTypes(history.getTicketTypes()),
            ); */
    }

    private TransactionHistoryDTO convertDebitToDTO(TransactionHistory history) {
        return TransactionHistoryDTO.builder()
                .id(history.getId())
                .transactionType(history.getTransactionType())
                .date(history.getDate())
                .ticketsCount(history.getTicketsCount())
                .studentDTO(convertToUserDTO(history.getStudent()))
                .porterDTO(convertToUserDTO(history.getPorter()))
                .ticketIds(parseTicketIds(history.getTicketIds()))
                .ticketTypes(parseTicketTypes(history.getTicketTypes()))
                .build();
    }

    private TransactionHistoryDTO convertTransferToDTO(TransactionHistory history) {
        return TransactionHistoryDTO.builder()
                .id(history.getId())
                .transactionType(history.getTransactionType())
                .date(history.getDate())
                .ticketsCount(history.getTicketsCount())
                .senderDTO(convertToUserDTO(history.getSender()))
                .recipientDTO(convertToUserDTO(history.getRecipient()))
                .ticketIds(parseTicketIds(history.getTicketIds()))
                .ticketTypes(parseTicketTypes(history.getTicketTypes()))
                .transferCanceled(history.getTransferCanceled())
                .build();
    }

    private UserDTO convertToUserDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleDTO(convertToRoleDTO(user.getRole()))
                .build();
    }

    private RoleDTO convertToRoleDTO(Role role) {
        if (role == null) return null;
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    private List<Long> parseTicketIds(String ticketIdsStr) {
        if (ticketIdsStr == null || ticketIdsStr.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(ticketIdsStr.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private List<String> parseTicketTypes(String ticketTypesStr) {
        if (ticketTypesStr == null || ticketTypesStr.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(ticketTypesStr.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    // Méthodes utilitaires pour enregistrer les transactions

    @Transactional
    public TransactionHistory recordPurchase(User purchaseUser, List<Ticket> tickets ) {
        String ticketIds = tickets.stream()
                .map(t -> t.getId().toString())
                .collect(Collectors.joining(","));

        String ticketTypes = tickets.stream()
                .map(t -> t.getType().name())
                .collect(Collectors.joining(","));

        TransactionHistory history = TransactionHistory.builder()
                .transactionType(TransactionType.PURCHASE)
                .date(LocalDateTime.now())
                .ticketsCount(tickets.size())
                .purchaser(purchaseUser)
                .ticketIds(ticketIds)
                .ticketTypes(ticketTypes)
                .build();

        log.info("Recorded purchase transaction for user: {}, tickets: {}", purchaseUser.getUsername(), tickets.size());
        return transactionHistoryRepository.save(history);

    }

    @Transactional
    public TransactionHistory recordDebit(User porter, User student, List<Ticket> tickets) {
        String ticketIds = tickets.stream()
                .map(t -> t.getId().toString())
                .collect(Collectors.joining(","));

        String ticketTypes = tickets.stream()
                .map(t -> t.getType().name())
                .collect(Collectors.joining(","));

        TransactionHistory history = TransactionHistory.builder()
                .transactionType(TransactionType.DEBIT)
                .date(LocalDateTime.now())
                .ticketsCount(tickets.size())
                .porter(porter)
                .student(student)
                .ticketIds(ticketIds)
                .ticketTypes(ticketTypes)
                .build();

        log.info("Recorded debit transaction by porter: {} for student: {}, tickets: {}",
                porter.getUsername(), student.getUsername(), tickets.size());
        return transactionHistoryRepository.save(history);
    }

    @Transactional
    public TransactionHistory recordTransfer(User sender, User recipient, List<Ticket> tickets) {
        String ticketIds = tickets.stream()
                .map(t -> t.getId().toString())
                .collect(Collectors.joining(","));

        String ticketTypes = tickets.stream()
                .map(t -> t.getType().name())
                .collect(Collectors.joining(","));

        TransactionHistory history = TransactionHistory.builder()
                .transactionType(TransactionType.TRANSFER)
                .date(LocalDateTime.now())
                .ticketsCount(tickets.size())
                .sender(sender)
                .recipient(recipient)
                .ticketIds(ticketIds)
                .ticketTypes(ticketTypes)
                .transferCanceled(false)
                .build();

        log.info("Recorded transfer transaction from: {} to: {}, tickets: {}",
                sender.getUsername(), recipient.getUsername(), tickets.size());
        return transactionHistoryRepository.save(history);
    }

    @Transactional
    public void cancelTransfer(Long transactionId) {
        TransactionHistory history = transactionHistoryRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (history.getTransactionType() != TransactionType.TRANSFER) {
            throw new IllegalStateException("Only transfer transactions can be cancelled");
        }

        history.setTransferCanceled(true);
        transactionHistoryRepository.save(history);
    }
}
