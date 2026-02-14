package sn.estm.managingrestauranttickets.controllers;

import sn.estm.managingrestauranttickets.dto.ConsulterMenuDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.ConsulterMenuService;

import java.util.List;

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

@Slf4j
@Data
@RestController
@RequestMapping("/api/consulter_menus")
@RequiredArgsConstructor
public class ConsulterMenuController {

    private final ConsulterMenuService consulterMenuService;


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ConsulterMenuDTO> createConsulterMenu(
                                                        @RequestBody ConsulterMenuDTO consulterMenuDTO) {

        log.info("Creating consulterMenu with details: {}", consulterMenuDTO);
       
        ConsulterMenuDTO createdConsulterMenu = consulterMenuService.createConsulterMenu(consulterMenuDTO);
       
        log.info("ConsulterMenu created successfully with ID: {}", 

        createdConsulterMenu.getConsulterMenuId());
       
        return new ResponseEntity<>(createdConsulterMenu, HttpStatus.CREATED);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<ConsulterMenuDTO>> getAllConsulterMenus() {

        List<ConsulterMenuDTO> consulterMenus = consulterMenuService.readConsulterMenus();

        log.info("Fetched consulterMenus: {}", consulterMenus);

        return new ResponseEntity<>(consulterMenus, HttpStatus.OK);
    }


    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{consulterMenuId}", produces = "application/json")
    public ResponseEntity<ConsulterMenuDTO> getConsulterMenuById(
                                                 @PathVariable Long consulterMenuId) {

        log.info("Fetched consulterMenu with ID: {}", consulterMenuId);

        ConsulterMenuDTO consulterMenuDTO = consulterMenuService.readConsulterMenuById(consulterMenuId);
       

        return new ResponseEntity<>(consulterMenuDTO, HttpStatus.OK);
    }

}
