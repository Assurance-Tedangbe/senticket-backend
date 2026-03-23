package sn.estm.managingrestauranttickets.dto.historydto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryResponseDTO { // DTO de réponse paginée

    private List<TransactionHistoryDTO> content;       // Liste des transactions
    private long totalElements;                        // Nombre total d'éléments
    private int totalPages;                            // Nombre total de pages
    private int currentPage;                           // Page actuelle (0-indexé)
    private int pageSize;                              // Taille de la page
    private boolean first;                             // Est-ce la première page ?
    private boolean last;                              // Est-ce la dernière page ?
    private boolean hasNext;                           // Y a-t-il une page suivante ?
    private boolean hasPrevious;                       // Y a-t-il une page précédente ?
}
