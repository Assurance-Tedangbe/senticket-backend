package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name="accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Serializable {

    @Column(unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;

	@Column(length = 70, nullable = false, unique = true)
    @NotBlank(message = "The account needs an account number.")
    @Size(min = 3, max = 70)
	private String accountNumber;

	@Column(nullable = false)
	private Double balance;

	@Temporal(TemporalType.DATE) //pour stocker que la date
	private LocalDate dateCreation;

	@OneToOne
	@JoinColumn(name = "user_Id", nullable = false)
    //@JsonBackReference // to deal with bidirectional relationships in Jackson infinite recursion problem
	private User user;

	private boolean active;
	
}

