package sn.estm.managingrestauranttickets.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sn.estm.managingrestauranttickets.entities.Menu;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.MenuService;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    MenuService menuService;

    
     @GetMapping()
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }
    @PostMapping()
    public void addMenu(@RequestBody Menu newMenu) {
        menuService.createMenu(newMenu);
    }

    @GetMapping("/{idMenu}")
    public Menu getMenu(@PathVariable("idMenu") Long idMenu) {
        return menuService.getMenuById(idMenu);
    }
    @PutMapping("/{id}")
    public void updateEleve(@PathVariable("id") Long idMenu,@RequestBody  Menu menu){
        menuService.updateMenu(idMenu, menu);
    }
    @DeleteMapping("/{id}")
    public void deleteEleve(@PathVariable("id") Long idMenu) {
       menuService.deleteMenuById(idMenu);
    }
    
}
