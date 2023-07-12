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
@Table(name = "Debiter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Debiter implements Serializable{
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long idDebit;
	@Temporal(TemporalType.DATE)
	private LocalDate dateDebit;
	
	@ManyToOne
	@JoinColumn(name="idCpt")
	@JsonBackReference 
	private Compte compte;
	
	@ManyToOne
	@JoinColumn(name="idPortier")
	@JsonBackReference 
	private Portier portier;
}
