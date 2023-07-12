package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.util.List;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu implements Serializable{
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long idMenu;
	@Column(length=30)
	private String typeMenu;
	
	@OneToMany(mappedBy="menu",cascade=CascadeType.ALL)
	@JsonManagedReference
	List<Ticket> ticket;
	
	@OneToOne
	@JoinColumn(name="idEtudiant")
	private Etudiant etudiant;
}
