package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;


@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket implements Serializable {

   @Column(unique = true, nullable = false)
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   @Enumerated(EnumType.STRING)
   private TicketType type;

   /* Price is derived from the type, so no default needed here
    * The price must be calculated and set in the business logic (Service layer) */
   @Column(nullable = false)
   private Double price;

   // Use the @Builder.Default annotation for Lombok's @Builder to respect the default
   @Column(nullable = false)
   @Builder.Default
   private boolean booked = false;

   @Column(nullable = false)
   @Enumerated(EnumType.STRING)
   @Builder.Default
   TicketStatus status = TicketStatus.AVAILABLE;

   /* Ticket Creation Date (Automatic value on creation)
    * updatable=false ensures it's only set once
    * @CreationTimestamp to set the value on insertion */
   @CreationTimestamp
   @Temporal(TemporalType.TIMESTAMP)
   @Column(nullable = false, updatable = false)
   private LocalDateTime creationDate;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id")
   private User user;
}


