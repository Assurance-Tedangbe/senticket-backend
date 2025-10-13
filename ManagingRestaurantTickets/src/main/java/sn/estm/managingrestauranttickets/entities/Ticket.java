package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket implements Serializable {

   @Column(unique = true, nullable = false)
   @Id
   @GeneratedValue(strategy =GenerationType.IDENTITY)
   private Long ticketId;
   
   @Size(min = 3, max = 50)
   @NotBlank(message = "The ticket needs a type.")
   private String ticketType;
   
   @Column(nullable = false)
   private double ticketPrice;

   @Column(unique = true, nullable = false)
   private Integer payementCode;

   @Column(nullable = false)
   private boolean booked;

   @Temporal(TemporalType.DATE)
   private LocalDate ticketIssueDate;

   @Size(min = 3, max = 100)
   @NotBlank(message = "The ticket needs a description.")
   private String ticketDescription;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name="account_id")
   private Account account;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name="user_id")
   private User user;
   
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name="menu_id")
   private Menu menu;
}
