// Cette entité stocke temporairement les informations du panier d'achat
// pendant que l'utilisateur est redirigé vers PayDunya( en attendant la confirmation PayDunya).
// Cela permet de récupérer la commande après le retour de l'utilisateur.

package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.enumerations.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingPayment {

    // Identifiant unique du paiement temporaire
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant unique de la transaction PayDunya (token) */
    @Column(unique = true, nullable = false)
    private String transactionId;

    /** ID de l'utilisateur qui a initié le paiement */
    @Column(nullable = false)
    private Long userId;

    /** Nombre de tickets Type A commandés */
    @Column(nullable = false)
    @Builder.Default
    private Integer countA = 0;

    /** Nombre de tickets Type B commandés */
    @Column(nullable = false)
    @Builder.Default
    private Integer countB = 0;

    /** Montant total du paiement en FCFA */
    private Double amount;

    /** Statut:PENDING (en attente), COMPLETED(réussi), FAILED */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    /** Date de création de l'enregistrement */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Date de dernière mise à jour */
    private LocalDateTime updatedAt;

    /** Méthode appelée automatiquement avant la persistance
     *  Initialise la date de création et le statut */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = PaymentStatus.PENDING;
    }

    /** Méthode appelée automatiquement avant la mise à jour
     *  Met à jour la date de modification */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

