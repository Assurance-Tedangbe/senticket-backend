package sn.estm.managingrestauranttickets.entities;

import java.io.Serializable;
import java.time.LocalDate;

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
@Table(name = "consulter_menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsulterMenu implements Serializable {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long consulterMenuId;

    @Temporal(TemporalType.DATE)
    private LocalDate consultationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="menu_id")
    private Menu menu;
}
