/* This interface is responsible for mapping Ticket entities to their DTOs and vice versa. */
package sn.estm.managingrestauranttickets.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.estm.managingrestauranttickets.dto.TicketDTO;
import sn.estm.managingrestauranttickets.entities.Ticket;


@Mapper(componentModel = "spring", uses = {
        UserMapper.class
       // , MenuMapper.class
})
public interface TicketMapper {

   // @Mapping(source = "menu", target = "menuDTO")
    @Mapping(source = "user", target = "userDTO")
    TicketDTO toDto(Ticket ticket);

    @Mapping(target = "user", ignore = true)
    Ticket toEntity(TicketDTO ticketDTO);

    List<TicketDTO> toDtoSet(List<Ticket> tickets);

    Set<Ticket> toEntitySet(Set<TicketDTO> ticketDTOs); 
}