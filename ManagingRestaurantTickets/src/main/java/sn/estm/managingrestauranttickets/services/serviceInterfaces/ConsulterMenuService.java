package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import java.util.List;

import sn.estm.managingrestauranttickets.dto.ConsulterMenuDTO;

public interface ConsulterMenuService {

    ConsulterMenuDTO createConsulterMenu(ConsulterMenuDTO consulterMenuDTO);

    List<ConsulterMenuDTO> readConsulterMenus();

    ConsulterMenuDTO readConsulterMenuById(Long consulterMenuId);

    ConsulterMenuDTO updateConsulterMenu(ConsulterMenuDTO consulterMenuDTO);

    void deleteConsulterMenu(Long consulterMenuId);
}
