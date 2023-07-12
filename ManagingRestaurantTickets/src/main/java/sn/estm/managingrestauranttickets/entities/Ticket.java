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
@Table(name = "Ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket implements Serializable {
   @Id
   @GeneratedValue(strategy =GenerationType.IDENTITY)
   private Long idTicket;
   private double prix;
   private Integer codePayement;
   private boolean reserve;
   
   @ManyToOne
   @JoinColumn(name="idEtudiant")
   @JsonBackReference
   private Etudiant etudiant;
   
   @ManyToOne
   @JoinColumn(name="idMenu")
   @JsonBackReference 
   private Menu menu;
}
