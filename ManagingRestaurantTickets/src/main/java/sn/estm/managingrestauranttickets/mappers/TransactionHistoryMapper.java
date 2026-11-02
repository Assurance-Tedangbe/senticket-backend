package sn.estm.managingrestauranttickets.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.estm.managingrestauranttickets.dto.historydto.TransactionHistoryDTO;
import sn.estm.managingrestauranttickets.entities.TransactionHistory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { UserMapper.class})
public interface TransactionHistoryMapper {

    @Mapping(source = "purchaser", target = "purchaserDTO")
    @Mapping(source = "porter", target = "porterDTO")
    @Mapping(source = "student", target = "studentDTO")
    @Mapping(source = "sender", target = "senderDTO")
    @Mapping(source = "recipient", target = "recipientDTO")
    @Mapping(target = "ticketIds", expression = "java(mapTicketIds(transactionHistory.getTicketIds()))")
    TransactionHistoryDTO toDto(TransactionHistory transactionHistory);

    // Méthodes de mapping personnalisées
    default List<Long> mapTicketIds(String ticketIdsStr) {
        if (ticketIdsStr == null || ticketIdsStr.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(ticketIdsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}


