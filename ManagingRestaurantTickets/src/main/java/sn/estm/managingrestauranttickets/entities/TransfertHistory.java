package sn.estm.managingrestauranttickets.entities;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransfertHistory {

    @Column(unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;*/

    @NotEmpty(message = "At least one ticket ID must be provided")
    @Column(name = "ticket_ids_transfered")
    private String ticketIdsTransfered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private LocalDateTime transferDate;

    @PrePersist
    protected void onCreate() {
        transferDate = LocalDateTime.now();
    }
}

