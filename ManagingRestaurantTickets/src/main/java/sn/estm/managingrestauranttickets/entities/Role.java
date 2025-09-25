package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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

   /* Genères l'association OneToMany entre Role et User en te basant sur l'attribut "role" dans l'entité User. */
   @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
   @Builder.Default
   private Set<User> users = new HashSet<>();

    /*
     * Set ne permet pas de doublons. Chaque User ne peut apparaître
     * qu'une seule fois dans le Set.
     * avec List, le même utilisateur peut être ajouté plusieurs fois.
     */
}
