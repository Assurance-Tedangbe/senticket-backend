package sn.estm.managingrestauranttickets.services.serviceImpl;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.dto.MenuDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.entities.Menu;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.MenuMapper;
import sn.estm.managingrestauranttickets.repositories.MenuRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.MenuService;

import java.util.List;

import java.text.MessageFormat;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;


    @Override
    public MenuDTO createMenu(MenuDTO menuDTO) {

        log.info("Creating menu with details: {}", menuDTO);
        
        Menu menu = menuMapper.toEntity(menuDTO);

        Menu savedMenu = menuRepository.save(menu);
        
        log.info("Menu created successfully with ID: {}", savedMenu.getId());
       
        return menuMapper.toDto(savedMenu);
    }


    @Override
    public List<MenuDTO> readMenus() {

        List<Menu> menus = menuRepository.findAll();
        
        return menus.stream()
                .map(menuMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public MenuDTO readMenuById(Long menuId) {
        
        log.info("Reading menu by menuId: {}", menuId);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Menu not found with ID: {0}", menuId)));

        return menuMapper.toDto(menu);
    }
    

    @Override
    public MenuDTO updateMenu(MenuDTO menuDTO) {
       
        log.info("Updating menu details: {}", menuDTO);

        Menu existingMenu = menuRepository.findById(menuDTO.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Menu not found with ID: {0}", menuDTO.getMenuId())));
                    
        existingMenu.setName(menuDTO.getMenuName());
        existingMenu.setType(menuDTO.getMenuType());
        existingMenu.setDescription(menuDTO.getMenuDescription());
        
        Menu updatedMenu = menuRepository.save(existingMenu);

        log.info("Menu updated successfully with name: {}", updatedMenu.getName());
        
        return menuMapper.toDto(updatedMenu);
    }


    @Override
    public void deleteMenu(Long menuId) {
       
        log.info("Deleting menu with menuId: {}", menuId);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Menu not found with ID: {0}", menuId)));
                    
        menuRepository.delete(menu);

        log.info("deleteMenu end ok - menuId: {}", menuId);
    }
}