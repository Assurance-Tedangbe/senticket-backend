// Stocke les informations du panier en attendant la confirmation PayDunya
// Cela permet de récupérer la commande après le retour de l'utilisateur

package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingPayment {

    /** Identifiant unique du paiement temporaire */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant de la transaction PayDunya (unique) */
    @Column(unique = true, nullable = false)
    private String transactionId;

    /** ID de l'utilisateur qui a initié le paiement */
    private Long userId;

    /** Montant du paiement */
    private Double amount;

    /** Description de la commande */
    private String description;

    /** Statut: PENDING, COMPLETED, CANCELLED, FAILED */
    private String status;

    /** Date de création de l'enregistrement */
    private LocalDateTime createdAt;

    /** Date de dernière mise à jour */
    private LocalDateTime updatedAt;

    /**
     * Méthode appelée automatiquement avant la persistance
     * Initialise la date de création et le statut
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = "PENDING";
    }

    /**
     * Méthode appelée automatiquement avant la mise à jour
     * Met à jour la date de modification
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}