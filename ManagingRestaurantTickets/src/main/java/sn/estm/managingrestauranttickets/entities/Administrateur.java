package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Administrateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Administrateur implements Serializable{
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long idAdmin;
	@Column(length=25)
	private String nomAdmin;
	@Column(length=25)
	private String prenomAdmin;
}
