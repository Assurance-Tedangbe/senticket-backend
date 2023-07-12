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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Portier")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portier implements Serializable {
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long idPortier;
	@Column(length=25)
	private String nomPortier;
	@Column(length=25)
	private String prenomPortier;
	private Long telPortier;
	@Column(length=10)
	private String sexePortier;
	
	@OneToMany(mappedBy="portier",cascade=CascadeType.ALL)
	@JsonManagedReference
	List<Debiter> listDebits;
	
	@OneToMany(mappedBy="portier",cascade=CascadeType.ALL)
	@JsonManagedReference
	List<Etudiant> etudiant;
}
