package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

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
	@Column(unique = true, nullable = false)
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=70, nullable = false, unique = true)
	@Size(min = 3, max = 70)
	private String name;

	@Column(length=30)
	@Size(min = 3, max = 30)
	private String type;

	@Column(length=100)
	@Size(min = 3, max = 100)
	private String description;
}
