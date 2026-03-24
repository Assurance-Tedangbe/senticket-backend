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
    @Mapping(target = "ticketTypes", expression = "java(mapTicketTypes(transactionHistory.getTicketTypes()))")
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

    default List<String> mapTicketTypes(String ticketTypesStr) {
        if (ticketTypesStr == null || ticketTypesStr.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(ticketTypesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    default String mapTicketIdsToString(List<Long> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return "";
        }
        return ticketIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    default String mapTicketTypesToString(List<String> ticketTypes) {
        if (ticketTypes == null || ticketTypes.isEmpty()) {
            return "";
        }
        return String.join(",", ticketTypes);
    }
}


