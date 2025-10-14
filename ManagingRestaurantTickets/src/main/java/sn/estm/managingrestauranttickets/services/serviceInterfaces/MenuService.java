 package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.dto.MenuDTO;

public interface MenuService {

    MenuDTO createMenu(MenuDTO menuDTO);

    List<MenuDTO> readMenus();

    MenuDTO readMenuById(Long menuId);

    MenuDTO updateMenu(MenuDTO menuDTO);

    void deleteMenu(Long menuId);
    
}
