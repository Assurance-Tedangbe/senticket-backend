package sn.estm.managingrestauranttickets.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import jakarta.validation.Valid;

//import org.springframework.security.access.prepost.PostAuthorize;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitAccountRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.PurchaseTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.dto.statisticsDTO.TicketStatisticsDTO;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Data
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TicketDTO>> getAllTickets() {

        List<TicketDTO> tickets = ticketService.readTickets();

        log.info("Fetched tickets: {}", tickets);

        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }


    //@PostAuthorize("hasAnyAuthority('ETUDIANT')")
    @PutMapping(value = "/purchase", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> purchaseTickets(@Valid @RequestBody PurchaseTicketsRequestDTO purchaseTicketsRequestDTO) {

        List<TicketDTO> purchasedTickets = ticketService.purchaseTickets(purchaseTicketsRequestDTO);

        log.info("Tickets purchased successfully: {}", purchasedTickets);

        return new ResponseEntity<>(purchasedTickets, HttpStatus.OK);
    }

    //@PostAuthorize("hasAuthority('PORTIER')")
    @PutMapping(value = "/debit", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void debitAccount(@Valid @RequestBody DebitAccountRequestDTO debitAccountRequestDTO) {

        log.info("Debiting account with request {}:", debitAccountRequestDTO);

        ticketService.debitAccount(debitAccountRequestDTO);

        log.info("Debited student account successfully {}", debitAccountRequestDTO);
    }

    @GetMapping(value = "/user/{userId}/purchased", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> getPurchasedTicketsByUser(
            @PathVariable Long userId,
            @RequestParam TicketType ticketType) {

        log.info("Requête pour récupérer les tickets achetés - User ID: {}, Ticket Type: {}",
                userId, ticketType);

        List<TicketDTO> purchasedTickets = ticketService.getPurchasedTicketsByUser(userId, ticketType);

        log.info("Retour de {} tickets achetés pour l'utilisateur ID: {} avec type: {}",
                purchasedTickets.size(), userId, ticketType);

        return new ResponseEntity<>(purchasedTickets, HttpStatus.OK);
    }

    // avec paramètres optionnels
    @GetMapping(value = "/user/{userId}/filter", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> getTicketsByUserWithFilters(
            @PathVariable Long userId,
            @RequestParam(required = false) TicketType ticketType,
            @RequestParam(required = false) Boolean booked,
            @RequestParam(required = false) TicketStatus ticketStatus) {

        log.info("Requête pour récupérer les tickets avec filtres - User ID: {}, Type: {}, Booked: {}, Status: {}",
                userId, ticketType, booked, ticketStatus);

        // Créer un service plus générique si nécessaire
        // Pour l'instant, nous utilisons le service existant avec une logique conditionnelle
        if (ticketType != null) {
            // Appeler la méthode spécifique
            List<TicketDTO> tickets = ticketService.getPurchasedTicketsByUser(userId, ticketType);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } else {
            // Implémenter une méthode plus générique si besoin
            throw new IllegalArgumentException("Le paramètre ticketType est requis pour cette version");
        }
    }

    @PutMapping(value = "/transferTickets", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionHistoryDTO> transferTickets(@RequestBody TransferTicketsRequestDTO transferTicketsRequestDTO) {

        log.info("Processing ticket transfer request {}:", transferTicketsRequestDTO);

        TransactionHistoryDTO historyDTO = ticketService.transferTickets(transferTicketsRequestDTO);

        log.info("Transfer of tickets completed successfully {}", transferTicketsRequestDTO);

        return ResponseEntity.ok(historyDTO);
    }

    @PutMapping(value = "/cancelTransfer", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void cancelTransferTickets(
            @Valid @RequestBody CancelTransferTicketsRequestDTO cancelTransferTicketsRequestDTO) {

        log.info("Processing cancel transfer request for transaction {}",
                cancelTransferTicketsRequestDTO.getCancelTransferDTO().getTransactionId());

        ticketService.cancelTransferTickets(cancelTransferTicketsRequestDTO);

        log.info("Transfer cancelled successfully {} at {}", cancelTransferTicketsRequestDTO, LocalDateTime.now());
    }

    /**
     * Endpoint pour récupérer les statistiques des tickets
     * @param userId (optionnel) - ID de l'utilisateur pour les stats spécifiques
     * @return TicketStatisticsDTO contenant toutes les statistiques
     */
    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TicketStatisticsDTO> getTicketStatistics(
            @RequestParam(value = "userId", required = false) Long userId) {

        log.info("GET statistics with userId: {}", userId);

        TicketStatisticsDTO statistics = ticketService.getTicketStatistics(userId);

        return ResponseEntity.ok(statistics);
    }
}
   /*
    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{ticketId}", produces = "application/json")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long ticketId) {

        log.info("Fetched user with ID: {}", ticketId);

        TicketDTO ticket = ticketService.readTicketById(ticketId);

        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/ticketStatus/{ticketStatus}", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> readTicketsByStatus(@PathVariable TicketStatus ticketStatus) {

        List<TicketDTO> ticketsByStatus = ticketService.readTicketsByStatus(ticketStatus);

         log.info("Fetched tickets by status: {}", ticketsByStatus);

        return new ResponseEntity<>(ticketsByStatus, HttpStatus.OK);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/userId/{userId}", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> readTicketsByUserId(@PathVariable Long userId) {

        List<TicketDTO> ticketsByUserId = ticketService.readTicketsByUserId(userId);

        log.info("Fetched tickets by a given userId: {}", userId);

        return new ResponseEntity<>(ticketsByUserId, HttpStatus.OK);
    }
     */