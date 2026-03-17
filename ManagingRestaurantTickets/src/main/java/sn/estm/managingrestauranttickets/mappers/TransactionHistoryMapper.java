package sn.estm.managingrestauranttickets.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;

@Mapper(componentModel = "spring", uses = { UserMapper.class})
public interface TransactionHistoryMapper {

    @Mapping(source = "purchaser", target = "purchaserDTO")
    @Mapping(source = "porter", target = "porterDTO")
    @Mapping(source = "student", target = "studentDTO")
    @Mapping(source = "sender", target = "senderDTO")
    @Mapping(source = "recipient", target = "recipientDTO")
    TransactionHistoryDTO toDto(TransactionHistory transactionHistory);
}
