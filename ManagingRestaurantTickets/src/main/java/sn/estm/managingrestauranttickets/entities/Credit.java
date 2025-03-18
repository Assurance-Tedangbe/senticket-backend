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
@Table(name = "credits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credit implements Serializable {
   @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long creditId;
	@Temporal(TemporalType.DATE)
	private LocalDate creditDate;

	@ManyToOne
	@JoinColumn(name="accountId")
	@JsonBackReference 
	private Account account;

	@ManyToOne
	@JoinColumn(name="userId")
	@JsonBackReference
	private User user;

}
