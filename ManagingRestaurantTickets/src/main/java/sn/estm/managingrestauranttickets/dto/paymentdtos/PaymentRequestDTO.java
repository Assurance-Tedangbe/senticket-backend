// DTO reçu du frontend pour initier un paiement
// Contient les informations du panier et du client

package sn.estm.managingrestauranttickets.dto.paymentdtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    /** Identifiant de l'utilisateur qui effectue le paiement */
    @NotNull(message = "L'ID utilisateur est requis")
    private Long userId;

    /** Montant total à payer */
    @NotNull(message = "Le montant est requis")
    @Positive(message = "Le montant doit être positif")
    private Double amount;

    /** Description de la commande */
    private String description;

    /** Liste des articles achetés */
    private List<PaymentItemDTO> items;

    /** Nom du client (optionnel) */
    private String customerName;

    /** Email du client (optionnel) */
    private String customerEmail;

    /** Téléphone du client (optionnel) */
    private String customerPhone;
}