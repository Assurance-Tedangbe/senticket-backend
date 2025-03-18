package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDate;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
    @Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long accountId;
	@Column(length=25)
	private String accountNumber;
	private Double balance;
	@Temporal(TemporalType.DATE) //pour stocker que la date
	private LocalDate dateCreation;

	@OneToOne
	@JoinColumn(name="userId")
	@JsonBackReference
	// to deal with bidirectional relationships in Jackson
	//infinite recursion problem
	private User user;
	
	@OneToMany(mappedBy="account",cascade=CascadeType.ALL)
	@JsonManagedReference 
	List<Credit> listCredits;
	
	@OneToMany(mappedBy="account",cascade=CascadeType.ALL)
	@JsonManagedReference
	List<Debit> listDebits;
	
    /* 	public Compte() {
	listCredits = new ArrayList<>();
	listDebits = new ArrayList<>();
	}
    */


}
