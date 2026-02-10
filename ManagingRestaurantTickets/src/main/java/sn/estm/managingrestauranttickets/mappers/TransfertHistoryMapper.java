package sn.estm.managingrestauranttickets.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.estm.managingrestauranttickets.dto.historydto.TransfertHistoryDTO;
import sn.estm.managingrestauranttickets.entities.TransfertHistory;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class, TicketMapper.class })
public interface TransfertHistoryMapper {

    @Mapping(source = "sender", target = "senderDTO")
    @Mapping(source = "recipient", target = "recipientDTO")
    @Mapping(source = "ticket", target = "ticketDTO")
    TransfertHistoryDTO toDto(TransfertHistory transfertHistory);

    //@Mapping(target = "senderDTO", ignore = true)
    TransfertHistory toEntity(TransfertHistoryDTO transfertHistoryDTO);

    List<TransfertHistoryDTO> toDtoSet(List<TransfertHistory> transfertHistories);

    Set<TransfertHistory> toEntitySet(Set<TransfertHistoryDTO> transfertHistoryDTOS);

}
