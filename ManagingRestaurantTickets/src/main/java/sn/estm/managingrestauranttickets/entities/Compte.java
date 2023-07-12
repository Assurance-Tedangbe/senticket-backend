package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Compte")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compte implements Serializable {
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long idCpt;
	@Column(length=25)
	private String numeroCompte;
	private Double solde;
	@Temporal(TemporalType.DATE) //pour stocker que la date
	private LocalDate dateCreation;
	
	@OneToOne
	@JoinColumn(name="idEtudiant")
	@JsonBackReference 
	// to deal with bidirectional relationships in Jackson
	//infinite recursion problem
	private Etudiant etudiant;
	
	@OneToMany(mappedBy="compte",cascade=CascadeType.ALL)
	@JsonManagedReference 
	List<Crediter> listCredits;
	
	@OneToMany(mappedBy="compte",cascade=CascadeType.ALL)
	@JsonManagedReference
	List<Debiter> listDebits;
	
/* 	public Compte() {
	listCredits = new ArrayList<>();
	listDebits = new ArrayList<>();
	}
    */
}
