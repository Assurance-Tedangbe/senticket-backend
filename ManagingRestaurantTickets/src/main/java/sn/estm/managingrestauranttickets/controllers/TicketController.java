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
import sn.estm.managingrestauranttickets.dto.customisedto.TicketCreationRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TicketFromDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TicketIdsToTransferDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.TransferedTicketIdsToCancelDTO;
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

     //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> createTicket(@RequestBody TicketCreationRequestDTO ticketCreationRequestDTO) {

        log.info("Creating tickets with requests: {}", ticketCreationRequestDTO );

        List<TicketDTO> createdTickets = ticketService.createTickets(ticketCreationRequestDTO);

        log.info("Tickets created successfully with IDs: {}", createdTickets);

        return new ResponseEntity<>(createdTickets, HttpStatus.CREATED);
    }
      
    
    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TicketDTO>> getAllTickets() {

        List<TicketDTO> tickets = ticketService.readTickets();

         log.info("Fetched tickets: {}", tickets);

        return new ResponseEntity<>(tickets, HttpStatus.OK);
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

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'ETUDIANT')")
    @PostMapping(value = "/{purchase}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<TicketDTO>> purchaseTickets(@Valid @RequestBody TicketFromDTO ticketFromDTO) {
        
        log.info("Purchasing ticket(s) with accountID with details: {}, {}",
        ticketFromDTO.getAccountDTO().getAccountId(),
        ticketFromDTO);
        
        // Call the service layer to execute the business logic
        List<TicketDTO> purchasedTickets = ticketService.purchaseTickets(ticketFromDTO);

        log.info("Tickets purchased successfully: {}", purchasedTickets);

        return new ResponseEntity<>(purchasedTickets, HttpStatus.CREATED);
    }

    //@PostAuthorize("hasAuthority('ADMIN', 'ETUDIANT')")
    @PutMapping(value = "/transferTickets")
    @ResponseStatus(HttpStatus.OK)
    public void transferTickets( @RequestBody TicketIdsToTransferDTO ticketIdToTransferDTO) {

        log.info("Transferring ticket(s) with details {}:", ticketIdToTransferDTO);

        ticketService.transferTickets(ticketIdToTransferDTO);

        log.info("Transfer of tickets completed successfully {}", ticketIdToTransferDTO);
    }

    //@PostAuthorize("hasAuthority('ADMIN', 'ETUDIANT')")
    @PutMapping(value = "/cancelTransferTickets")
    @ResponseStatus(HttpStatus.OK)
    public void  cancelTransferTickets(@RequestBody TransferedTicketIdsToCancelDTO
                                                   transferedTicketIdsToCancelDTO) {

        log.info("Cancelling transfer ticket(s) with details {}:",
                transferedTicketIdsToCancelDTO);

        ticketService.cancelTransferTickets(transferedTicketIdsToCancelDTO);

        log.info("Cancelled transfer tickets completed successfully {}",
                transferedTicketIdsToCancelDTO);
    }

    //@PostAuthorize("hasAuthority('ADMIN, PORTIER')")
    @PutMapping(value = "/debitAccount/{stutentAccountId}/by/{portierAccountId}")
    @ResponseStatus(HttpStatus.OK)
    public void debitAccount(@PathVariable Long portierAccountId,
                              @PathVariable Long studentAccountId,
                              @RequestBody Long ticketId) {

        log.info("Debiting account with ID {} for ticket with ID: {} by portierAccount {}",
                studentAccountId, ticketId, portierAccountId);

        ticketService.debitAccount(portierAccountId, studentAccountId, ticketId);

        log.info("Debited account {} by portier {} for the ticket with ID: {}",
                studentAccountId, portierAccountId, ticketId);
    }

}