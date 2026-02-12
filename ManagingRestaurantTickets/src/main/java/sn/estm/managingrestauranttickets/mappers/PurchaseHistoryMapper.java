package sn.estm.managingrestauranttickets.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.estm.managingrestauranttickets.dto.historydto.PurchaseHistoryDTO;
import sn.estm.managingrestauranttickets.entities.PurchaseHistory;


@Mapper(componentModel = "spring", uses = {
        UserMapper.class, TicketMapper.class })
public interface PurchaseHistoryMapper {

    @Mapping(source = "purchaseUser", target = "purchaseUserDTO")
    @Mapping(source = "ticket", target = "ticketDTO")
    PurchaseHistoryDTO toDto(PurchaseHistory purchaseHistory);

    PurchaseHistory toEntity(PurchaseHistoryDTO purchaseHistoryDTO);

    List<PurchaseHistoryDTO> toDtoSet(List<PurchaseHistory> purchaseHistories);

    Set<PurchaseHistory> toEntitySet(Set<PurchaseHistoryDTO> purchaseHistoryDTOS);
}
