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
import jakarta.persistence.PrePersist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "debit_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitHistory {

    @Column(unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debitHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debitPorter_id")
    private User debitPorter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debitStudent_id")
    private User debitStudent;

    private LocalDateTime debitDate;

    @PrePersist
    protected void onCreate() {
        debitDate = LocalDateTime.now();
    }
}
