package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Etudiant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etudiant implements Serializable{
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long idEtudiant;
	private Long numeroCarte;
	@Column(length=25)
	private String nom;
	@Column(length=25)
	private String prenom;
	@Column(length=50)
	private String filiere;
	private Long tel;
	@Column(length=10)
	private String sexeEtud;
	
	@OneToOne(mappedBy="etudiant", cascade=CascadeType.ALL)
	@JsonManagedReference
	private Compte compte;
	//@JsonProperty(access=Access.WRITE_ONLY)
	// to deal with bidirectional relationships in Jackson
	//infinite recursion problem
	
	@ManyToOne
	@JoinColumn(name="idPortier")
	@JsonBackReference 
	private Portier portier;
	
	@OneToMany(mappedBy="etudiant",cascade=CascadeType.ALL)
	@JsonManagedReference
	//@JsonProperty(access=Access.WRITE_ONLY)
	List<Ticket> listTickets;
}
