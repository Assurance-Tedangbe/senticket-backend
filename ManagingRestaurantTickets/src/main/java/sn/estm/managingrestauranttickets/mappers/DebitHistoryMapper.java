package sn.estm.managingrestauranttickets.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.estm.managingrestauranttickets.dto.historydto.DebitHistoryDTO;
import sn.estm.managingrestauranttickets.entities.DebitHistory;


@Mapper(componentModel = "spring", uses = {
        UserMapper.class, TicketMapper.class })
public interface DebitHistoryMapper {

    @Mapping(source = "debitStudent", target = "debitStudentDTO")
    @Mapping(source = "debitPorter", target = "debitPorterDTO")
    @Mapping(source = "ticket", target = "ticketDTO")
    DebitHistoryDTO toDto(DebitHistory debitHistory);

    /* DebitHistory toEntity(DebitHistoryDTO debitHistoryDTO);

    List<DebitHistoryDTO> toDtoSet(List<DebitHistory>debitHistories);

    Set<DebitHistory> toEntitySet(Set<DebitHistoryDTO> debitHistoryDTOS);*/
}
