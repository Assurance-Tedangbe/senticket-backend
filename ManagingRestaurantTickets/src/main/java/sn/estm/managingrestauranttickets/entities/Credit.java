package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDate;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    
	@Column(unique = true, nullable = false)
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long creditId;

	@Temporal(TemporalType.DATE)
	private LocalDate creditDate;

	@Column(nullable = false)
	private Double creditAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="account_id")
	@JsonBackReference 
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	@JsonBackReference
	private User user;

}
