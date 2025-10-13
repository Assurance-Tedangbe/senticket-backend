/* Mapper for MenuDTO and Menu entities */
package sn.estm.managingrestauranttickets.mappers;
import java.util.Set;

import org.mapstruct.Mapper;

import sn.estm.managingrestauranttickets.dto.MenuDTO;
import sn.estm.managingrestauranttickets.entities.Menu;


@Mapper(componentModel = "spring")
public interface MenuMapper {

    MenuDTO toDto(Menu menu);

    Menu toEntity(MenuDTO menuDTO);

    Set<MenuDTO> toDtoSet(Set<Menu> menus);

    Set<Menu> toEntitySet(Set<MenuDTO> menuDTOs);
}