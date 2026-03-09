package sn.estm.managingrestauranttickets.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    
    private Long id;

    @NotBlank(message = "The ticket needs a type.")
    private TicketType type;

    @NotNull(message = "The ticket price is required.")
    private Double price;

    @Column(nullable = false)
    @NotNull(message = "The booked status is required.")
    private boolean booked;

    @NotBlank(message = "The ticket status is required.")
    private TicketStatus status;

    @NotNull(message = "The ticket creation date is required.")
    private LocalDateTime creationDate;

    @Valid
    @NotNull(message = "The user is required.")
    private UserDTO userDTO;
}

    /*
    @Size(min = 3, max = 100)
    @NotBlank(message = "The ticket needs a description.")
    private String description;

    @Column(unique = true, nullable = false)
    @NotNull(message = "The payment code is required.")
    private String paymentCode;*/