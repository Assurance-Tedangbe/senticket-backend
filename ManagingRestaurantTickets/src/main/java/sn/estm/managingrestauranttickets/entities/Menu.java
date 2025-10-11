package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu implements Serializable{
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long menuId;
	@Column(length=30)
	private String menuType;
	
	@OneToMany(mappedBy="menu",cascade=CascadeType.ALL)
	@JsonManagedReference
	List<Ticket> ticket;

	@OneToOne
	@JoinColumn(name="userId")
	private User user;
	
	/*@OneToOne
	@JoinColumn(name="idEtudiant")
	private Etudiant etudiant;*/
}
