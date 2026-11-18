package sn.estm.managingrestauranttickets.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    @NotBlank(message = "The ticket status is required.")
    private TicketStatus status;

    @NotNull(message = "The ticket purchase date is required.")
    private LocalDateTime purchasedAt;

    private LocalDateTime usedAt;

    @Valid
    @NotNull(message = "The user is required.")
    private UserDTO userDTO;
}

