package sn.estm.managingrestauranttickets.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
     
    @Autowired
    TicketService ticketService;

    /*
        GET /users/{id}/orders        → Récupérer les commandes d’un utilisateur
        POST /users/{id}/orders       → Ajouter une commande pour un utilisateur
        GET /users/{id}/orders/{oid}  → Récupérer une commande spécifique

        /api/tickets
        Acheter ticket (par un Etudiant pour son compte) :        @PostMapping("/{idCpt}/ticket/")
        Transférer ticket(de compte à compte):      @PostMapping("{idCptExp}/compte/{idCptDest}")
        Annuler transfert ticket:  @PostMapping("/{ticketId}")
        getAllTickets
        updateTicket
     */

    @GetMapping()
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }
    @PostMapping()
    public void createEleve(@RequestBody Ticket newTicket) {
        ticketService.createTicket(newTicket);
    }

    @GetMapping("/{idTicket}")
    public Ticket getTicket(@PathVariable("idTicket") Long idTckt) {
        return ticketService.getTicketById(idTckt);
    }
    @PutMapping("/{id}")
    public void updateEleve(@PathVariable("id") Long idTicket,@RequestBody  Ticket ticket){
        ticketService.updateTicket(idTicket, ticket);
    }
    @DeleteMapping("/{id}")
    public void deleteEleve(@PathVariable("id") Long idTicket) {
       ticketService.deleteTicketById(idTicket);
    }
    
}
