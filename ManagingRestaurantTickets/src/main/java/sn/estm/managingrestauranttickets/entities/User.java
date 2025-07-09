package sn.estm.managingrestauranttickets.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

    @Column(nullable = false, unique = true)
    @NotBlank(message = "User needs a username.")
    @Size(min = 3, max = 100)
    private String username;

    @Column(nullable = false)
    @Size(min = 3, max = 100)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "UserFirstName")
    @Size(min = 3, max = 100)
    private String userFirstName;

    @Column(name = "UserLastName")
    @Size(min = 3, max = 100)
    private String userLastname;

    @Size(min = 3, max = 100)
    @Column(name = "UserEmailAddress")
    private String userEmailAddress;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles = new HashSet<>();


}
