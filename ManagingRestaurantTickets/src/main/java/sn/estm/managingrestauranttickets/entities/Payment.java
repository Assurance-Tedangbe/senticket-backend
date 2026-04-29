package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;
import sn.estm.managingrestauranttickets.enumerations.PaymentStatus;

/**
 * Entité pour suivre chaque tentative de paiement PayDunya.
 * Permet de faire le lien entre un paiement en cours et les tickets à acheter.
 * Utilisée pour confirmer l'achat après callback de PayDunya.
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Token unique retourné par PayDunya lors de la création de la facture.
     * Utilisé pour identifier le paiement dans les callbacks.
     */
    @Column(name = "paydunya_token", unique = true)
    private String paydunyaToken;

    /**
     * URL de paiement retournée par PayDunya.
     * L'utilisateur est redirigé vers cette URL pour payer.
     */
    @Column(name = "payment_url", length = 500)
    private String paymentUrl;

    /**
     * Statut du paiement : PENDING, COMPLETED, FAILED, CANCELLED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Montant total du paiement en FCFA
     */
    @Column(name = "amount", nullable = false)
    private Double amount;

    /**
     * IDs des tickets à acheter, stockés en CSV : "1,2,3"
     * Récupérés lors du callback pour finaliser l'achat
     */
    @Column(name = "ticket_ids")
    private String ticketIds;

    /**
     * L'utilisateur qui effectue l'achat
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Date de création du paiement
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de mise à jour du statut
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}





/*
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String orderId;

    private long amount;

    private String customerPhone;
    private String customerEmail;
    private String customerName;

    private String status; // PENDING, SUCCESS, CANCELLED, FAILED

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    */
/* PreUpdate marks the method to be executed automatically
     just before an existing entity is updated in the dbb *//*

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
*/
