package sn.estm.managingrestauranttickets.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.dto.MenuDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.MenuService;

import java.util.List;


@Slf4j
@Data
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<MenuDTO> createMenu(@RequestBody MenuDTO menuDTO) {

        log.info("Creating menu with details: {}", menuDTO);
       
        MenuDTO createdMenu = menuService.createMenu(menuDTO);
       
        log.info("Menu created successfully with ID: {}", createdMenu.getMenuId());
       
        return new ResponseEntity<>(createdMenu, HttpStatus.CREATED);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<MenuDTO>> getAllMenus() {

        List<MenuDTO> menus = menuService.readMenus();

        log.info("Fetched menus: {}", menus);

        return new ResponseEntity<>(menus, HttpStatus.OK);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{menuId}", produces = "application/json")
    public ResponseEntity<MenuDTO> getMenuById(@PathVariable Long menuId) {

        log.info("Fetched menu with ID: {}", menuId);

        MenuDTO menu = menuService.readMenuById(menuId);
       

        return new ResponseEntity<>(menu, HttpStatus.OK);
    }
}