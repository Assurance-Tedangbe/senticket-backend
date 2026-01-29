package sn.estm.managingrestauranttickets.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

//import org.springframework.security.access.prepost.PostAuthorize;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.DebitAccountRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CreationTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.PurchaseTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CancelTransferTicketsRequestDTO;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;

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

    /*//@PostAuthorize("hasAuthority('ADMIN', 'ETUDIANT')")
    @PutMapping(value = "/transferTickets")
    @ResponseStatus(HttpStatus.OK)
    public void transferTickets( @RequestBody TransferTicketsRequestDTO transferTicketsRequestDTO) {

        log.info("Transferring ticket(s) with details {}:", transferTicketsRequestDTO);

        ticketService.transferTickets(transferTicketsRequestDTO);

        log.info("Transfer of tickets completed successfully {}", transferTicketsRequestDTO);
    }

    //@PostAuthorize("hasAuthority('ADMIN', 'ETUDIANT')")
    @PutMapping(value = "/cancelTransferTickets")
    @ResponseStatus(HttpStatus.OK)
    public void  cancelTransferTickets(@RequestBody CancelTransferTicketsRequestDTO
                                               cancelTransferTicketsRequestDTO) {

        log.info("Cancelling transfer ticket(s) with details {}:",
                cancelTransferTicketsRequestDTO);

        ticketService.cancelTransferTickets(cancelTransferTicketsRequestDTO);

        log.info("Cancelled transfer tickets completed successfully {}",
                cancelTransferTicketsRequestDTO);
    }

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> createTicket(@RequestBody CreationTicketsRequestDTO creationTicketsRequestDTO) {

        log.info("Creating tickets with requests: {}", creationTicketsRequestDTO);

        List<TicketDTO> createdTickets = ticketService.createTickets(creationTicketsRequestDTO);

        log.info("Tickets created successfully with IDs: {}", createdTickets);

        return new ResponseEntity<>(createdTickets, HttpStatus.CREATED);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{ticketId}", produces = "application/json")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long ticketId) {

        log.info("Fetched user with ID: {}", ticketId);

        TicketDTO ticket = ticketService.readTicketById(ticketId);

        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{ticketId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable Long ticketId,
                                                  @RequestBody TicketDTO ticketDTO) {

        log.info("Updating ticket with ID: {} with details: {}", ticketId, ticketDTO);

        TicketDTO updatedTicket = ticketService.updateTicket(ticketDTO);

        log.info("Ticket updated successfully with ID: {}", updatedTicket.getTicketId());

        return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
    }

    //@PostAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable Long ticketId) {

        log.info("Deleting ticket with ID: {}", ticketId);

        ticketService.deleteTicket(ticketId);

        log.info("Ticket deleted successfully with ID: {}", ticketId);
    }

     //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @PutMapping(value = "/ticketStatus/{ticketId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updateTicketStatus(@PathVariable Long ticketId,
                                   @RequestBody TicketStatus ticketStatus) {

        log.info("Updating ticket status for ticket with ID: {}", ticketId);

        ticketService.updateTicketStatus(ticketId, ticketStatus);

        log.info("TicketStatus updated successfully for ticket with ID: {}", ticketId);
    }

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/book/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    public void bookTicket(@PathVariable Long ticketId) {

        log.info("Booking ticket with ID: {}", ticketId);

        ticketService.bookTicket(ticketId);

        log.info("Ticket booked successfully with ID: {}", ticketId);
    }

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/unbook/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    public void unbookTicket(@PathVariable Long ticketId) {

        log.info("Unbooking ticket with ID: {}", ticketId);

        ticketService.unbookTicket(ticketId);

        log.info("Ticket unbooked successfully with ID: {}", ticketId);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/ticketStatus/{ticketStatus}", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> readTicketsByStatus(@PathVariable TicketStatus ticketStatus) {

        List<TicketDTO> ticketsByStatus = ticketService.readTicketsByStatus(ticketStatus);

         log.info("Fetched tickets by status: {}", ticketsByStatus);

        return new ResponseEntity<>(ticketsByStatus, HttpStatus.OK);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/accountId/{accoundId}", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> readTicketsByAccountId(@PathVariable Long accountId) {

        List<TicketDTO> ticketsByAccountId = ticketService.readTicketsByAccountId(accountId);

         log.info("Fetched tickets by a given accountId: {}", accountId);

        return new ResponseEntity<>(ticketsByAccountId, HttpStatus.OK);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/userId/{userId}", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> readTicketsByUserId(@PathVariable Long userId) {

        List<TicketDTO> ticketsByUserId = ticketService.readTicketsByUserId(userId);

        log.info("Fetched tickets by a given userId: {}", userId);

        return new ResponseEntity<>(ticketsByUserId, HttpStatus.OK);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/menuId/{menuId}/userId/{userId}", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> readTicketsByMenuIdAndUserId(@PathVariable Long menuId,
                                                                        @PathVariable Long userId) {

        List<TicketDTO> ticketsByMenuIdandUserId = ticketService.
                                                   readTicketsByMenuIdAndUserId(menuId, userId);

        log.info("Fetched tickets by a given menuId and userId: {}, {}", menuId, userId);

        return new ResponseEntity<>(ticketsByMenuIdandUserId, HttpStatus.OK);
    }

     //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/menuId/{menuId}/userId/{userId}/status/{ticketStatus}", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> readTicketsByMenuIdAndUserIdAndStatus(@PathVariable Long menuId,
                                                                                 @PathVariable Long userId,
                                                                                 @PathVariable TicketStatus ticketStatus) {

        List<TicketDTO> ticketsByMenuIdandUserIdAndStatus = ticketService.
                                                 readTicketsByMenuIdAndUserIdAndStatus(
                                                    menuId, userId, ticketStatus);

        log.info("Fetched tickets by a given menuId, userId and status: {},{},{}", menuId, userId, ticketStatus);

        return new ResponseEntity<>(ticketsByMenuIdandUserIdAndStatus, HttpStatus.OK);
    }
     */
}