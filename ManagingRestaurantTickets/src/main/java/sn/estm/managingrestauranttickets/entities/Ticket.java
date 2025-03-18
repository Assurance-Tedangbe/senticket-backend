package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
   @Id
   @GeneratedValue(strategy =GenerationType.IDENTITY)
   private Long ticketId;
   private double price;
   private Integer payementCode;
   private boolean booked;

   // asso avec compte necessaire
   @ManyToOne
   @JoinColumn(name="accountId")
   @JsonBackReference
   private Account account;

   @ManyToOne
   @JoinColumn(name="userId")
   @JsonBackReference
   private User user;
   
   @ManyToOne
   @JoinColumn(name="idMenu")
   @JsonBackReference 
   private Menu menu;
}
