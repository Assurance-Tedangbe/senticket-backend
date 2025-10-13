/* This interface is responsible for mapping ConsulterMenu entities to their DTOs and vice versa. */
package sn.estm.managingrestauranttickets.mappers;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.estm.managingrestauranttickets.dto.ConsulterMenuDTO;
import sn.estm.managingrestauranttickets.entities.ConsulterMenu;


@Mapper(componentModel = "spring", uses = {MenuMapper.class, UserMapper.class})
public interface ConsulterMenuMapper {

    @Mapping(source = "menu", target = "menuDTO")
    @Mapping(source = "user", target = "userDTO")
    ConsulterMenuDTO toDto(ConsulterMenu consulterMenu);

    @Mapping(target = "menu", ignore = true) 
    @Mapping(target = "user", ignore = true)
    ConsulterMenu toEntity(ConsulterMenuDTO consulterMenuDTO);

    Set<ConsulterMenuDTO> toDtoSet(Set<ConsulterMenu> consulterMenus);

    Set<ConsulterMenu> toEntitySet(Set<ConsulterMenuDTO> consulterMenuDTOs);
}

