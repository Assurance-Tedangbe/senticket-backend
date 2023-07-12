package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDate;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
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

@Entity
@Table(name = "Crediter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crediter implements Serializable {
   @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long idCredit;
	@Temporal(TemporalType.DATE)
	private LocalDate dateCredit;
	
	@ManyToOne
	@JoinColumn(name="idCpt")
	@JsonBackReference 
	private Compte compte;
	
	@ManyToOne
	@JoinColumn(name="idVendeur")
	@JsonBackReference 
	private Vendeur vendeur; 
}
