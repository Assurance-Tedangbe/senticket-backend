package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Column(unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 70, nullable = false, unique = true)
    @NotBlank(message = "User needs a username.")
    @Size(min = 3, max = 70)
    private String username;

    @Column(nullable = false)
    @Size(min = 3, max = 100)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "firstName")
    @Size(min = 3, max = 100)
    private String firstName;

    @Column(name = "lastName")
    @Size(min = 3, max = 100)
    private String lastName;

    @Size(min = 3, max = 100)
    @Column(name = "email")
    private String email;

    /**
     * Représente la relation OneToMany entre l'utilisateur et les rôles.
     * Cette propriété permet d'associer un ou un seul rôle à chaque
     *  utilisateur.
     * Le chargement des rôles est effectué de manière immédiate (EAGER). 
     * Un utilisateur peut avoir un et un seul rôle.
     * Et chaque rôle peut être attribué à plusieurs utilisateurs
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
}
