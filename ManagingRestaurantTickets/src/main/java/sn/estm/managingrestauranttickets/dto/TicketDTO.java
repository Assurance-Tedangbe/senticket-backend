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
    
    private Long ticketId;

    @NotBlank(message = "The ticket needs a type.")
    private TicketType ticketType;

    @NotNull(message = "The ticket price is required.")
    private Double ticketPrice;

    @Column(unique = true, nullable = false)
    @NotNull(message = "The payment code is required.")
    private String paymentCode;

    @Column(nullable = false)
    @NotNull(message = "The booked status is required.")
    private boolean booked;

    @NotBlank(message = "The ticket status is required.")
    private TicketStatus ticketStatus;

    @NotNull(message = "The ticket creation date is required.")
    private LocalDateTime ticketCreationDate;

    @NotNull(message = "The ticket purchase date is required.")
    private LocalDateTime ticketPurchaseDate;

    @Size(min = 3, max = 100)
    @NotBlank(message = "The ticket needs a description.")
    private String ticketDescription;

    /*@Valid
    @NotNull(message = "The menu is required.")
    private MenuDTO menuDTO;*/

    @Valid
    @NotNull(message = "The user is required.")
    private UserDTO userDTO;

    @Valid
    @NotNull(message = "The account is required.")
    private AccountDTO accountDTO;
}