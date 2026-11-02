package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.enumerations.TransactionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history", indexes = {
        @Index(name = "idx_th_purchaser", columnList = "purchaser_id"),
        @Index(name = "idx_th_student",   columnList = "student_id"),
        @Index(name = "idx_th_sender",    columnList = "sender_id"),
        @Index(name = "idx_th_recipient", columnList = "recipient_id"),
        @Index(name = "idx_th_date",      columnList = "date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // PURCHASE, DEBIT, TRANSFER

    @Column(nullable = false)
    private Integer ticketsCount;

    /** Date de la transaction (auto-remplie par @PrePersist) */
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * IDs des tickets concernés, stockés en CSV "1,2,3".
     * Utilisé pour l'annulation de transfert et la traçabilité.
     */
    @Column(name = "ticket_ids")
    private String ticketIds;

    /** Résumé lisible des types de tickets concernés : "2xA, 1xB" */
    @Column(name = "ticket_summary", length = 100)
    private String ticketSummary;

    /**
     * Montant total de la transaction en FCFA.
     * Utile pour l'historique des achats.
     */
    @Column(name = "amount")
    private Double amount;

    // ── ACHAT (PURCHASE) ─────────────────────────────────────────
    /** L'étudiant qui a acheté les tickets */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaser_id")
    private User purchaser;

    // ── DÉBIT (DEBIT) ─────────────────────────────────────────────
    /** Le portier qui a effectué le débit */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "porter_id")
    private User porter;

    /** L'étudiant dont le compte a été débité */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    // ── TRANSFERT (TRANSFER) ──────────────────────────────────────
    /** L'étudiant qui envoie les tickets */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    /** L'étudiant qui reçoit les tickets */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(name = "transfer_canceled")
    private Boolean transferCanceled;

    @PrePersist
    protected void onCreate() {
        date = LocalDateTime.now();
    }
}
