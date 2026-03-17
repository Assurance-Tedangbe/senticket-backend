package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.enumerations.TransactionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistory {

    @Column(unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // PURCHASE, DEBIT, TRANSFER

    @Column(nullable = false)
    private Integer ticketsCount;

    @Column(nullable = false)
    private LocalDateTime date;

    // Champs communs
    /* @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;*/ // L'utilisateur principal concerné (acheteur, étudiant débité, sender)

    /* @Column(nullable = false)
    private String status; // SUCCESS, FAILED, CANCELLED*/

    // Champs optionnels pour les tickets (peut être une liste d'IDs ou une relation)
    @Column(name = "ticket_ids")
    private String ticketIds; // Stocke les ids des tickets concernés comme "1,2,3"

    @Column(name = "ticket_types")
    private String ticketTypes; // Stocke les types de tickets concernés comme "A,B"

    // Champs spécifiques aux ACHATS (PURCHASE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaser_id")
    private User purchaser; // L'acheteur des ticket(s)

    /* private Double totalAmount; // Montant total de l'achat*/

    // Champs spécifiques aux DÉBITS (DEBIT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "porter_id")
    private User porter; // Le portier qui a effectué le débit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student; // L'étudiant dont le compte a été débité

    // Champs spécifiques aux TRANSFERTS (TRANSFER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender; // L'expéditeur du transfert

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient; // Le destinataire du transfert

    @Column(name = "transfer_canceled")
    private Boolean transferCanceled; // Indique si le transfert a été annulé
}
