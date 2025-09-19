package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Size(min = 3, max = 100)
    @Column(nullable = false, unique = true)
    private String roleName;

    /**
     * Association bidirectionnelle Many-to-Many entre Role et User.
     * Cette collection représente l'ensemble des utilisateurs associés à ce rôle.
     * Le mapping est géré par l'attribut "roles" dans l'entité User.
     */
    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> users = new HashSet<>();

    /*
     * Set ne permet pas de doublons. Chaque User ne peut apparaître
     * qu'une seule fois dans le Set.
     * avec List, le même utilisateur peut être ajouté plusieurs fois.
     */
}
