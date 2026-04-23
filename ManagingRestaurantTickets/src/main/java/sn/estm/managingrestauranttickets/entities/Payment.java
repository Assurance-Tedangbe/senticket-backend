/*
package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String orderId;

    private long amount;

    private String customerPhone;
    private String customerEmail;
    private String customerName;

    private String status; // PENDING, SUCCESS, CANCELLED, FAILED

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    */
/* PreUpdate marks the method to be executed automatically
     just before an existing entity is updated in the dbb *//*

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
*/
