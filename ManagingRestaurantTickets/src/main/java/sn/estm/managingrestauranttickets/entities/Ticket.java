package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;


@Entity
@Table(name = "tickets", indexes = {
        @Index(name = "idx_ticket_user_id", columnList = "user_id"),
        @Index(name = "idx_ticket_status",  columnList = "status"),
        @Index(name = "idx_ticket_type",    columnList = "type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(unique = true, nullable = false)
   private Long id;

   /** Type du ticket : A (petit-déj, 50 FCFA) ou B (déj/dîner, 100 FCFA) */
   @NotNull
   @Column(nullable = false)
   @Enumerated(EnumType.STRING)
   private TicketType type;

   /* Price is derived from the type, so no default needed here
    * The price must be calculated and set in the business logic (Service layer) */
   /** Prix calculé et fixé à la création — 50.0 pour A, 100.0 pour B */
   @Column(nullable = false)
   private Double price;

   @NotNull
   @Column(nullable = false)
   @Enumerated(EnumType.STRING)
   @Builder.Default
   TicketStatus status = TicketStatus.BOOKED;

   /* updatable=false ensures it's only set once
    * @CreationTimestamp to set the value on insertion */
   /** Date de création du ticket (= date d'achat) */
   @CreationTimestamp
   @Temporal(TemporalType.TIMESTAMP)
   @Column(nullable = false, updatable = false, name = "purchased_at")
   private LocalDateTime purchasedAt;

   /** Date à laquelle le ticket a été débité (null si pas encore utilisé) */
   @Column(name = "used_at")
   private LocalDateTime usedAt;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

   // ── Méthodes utilitaires
   /** Retourne true si le ticket est en possession de l'étudiant */
   public boolean isAvailableForUse() {
      return status == TicketStatus.BOOKED;
   }

   /** Marque le ticket comme utilisé */
   public void markAsUsed() {
      this.status = TicketStatus.USED;
      this.usedAt = LocalDateTime.now();
   }
}


