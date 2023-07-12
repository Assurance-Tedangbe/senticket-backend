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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Vendeur")
public class Vendeur implements Serializable {
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long idVendeur;
	@Column(length=25)
	private String nomVendeur;
	@Column(length=25)
	private String prenomVendeur;
	private Long telVendeur;
	@Column(length=10)
	private String sexeVendeur;
	
	@OneToMany(mappedBy="vendeur",cascade=CascadeType.ALL)
	@JsonManagedReference
	List<Crediter> listCredits;
    
}
