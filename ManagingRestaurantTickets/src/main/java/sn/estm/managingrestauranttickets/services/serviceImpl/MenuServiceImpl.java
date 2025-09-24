/* package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.entities.Menu;
import sn.estm.managingrestauranttickets.repositories.MenuRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.MenuService;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService{

    @Autowired
    MenuRepository menuRepository;

    @Override
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    @Override
    public void createMenu(Menu menu) {
        menuRepository.save(menu);
        log.info("added object {}", menu);
    }

    @Override
    public Menu getMenuById(Long idMenu) {
        
        Optional<Menu> optional = menuRepository.findById(idMenu);
	    Menu menu = null;
		if(optional.isPresent())
		{
			menu = optional.get(); 
		}
		else
		{
			throw new RuntimeException("This object doesn't exist" +idMenu);
		}
		   return menu;
    }

    @Override
    public void updateMenu(Long idMenu, Menu newMenu) {
        Menu menu = this.getMenuById(idMenu);
        
        if(menu==null) 
        throw new UnsupportedOperationException("update failed");
        
        else{
          menu.setMenuId(newMenu.getMenuId());
          menu.setMenuType(newMenu.getMenuType());
          menu.setTicket(newMenu.getTicket());
          menu.setUser(newMenu.getUser());
          menuRepository.save(menu);
          log.info("returned to postaman the update object {}", menu);
        }
    }

    @Override
    public void deleteMenuById(Long idMenu) {
        menuRepository.deleteById(idMenu);
    }
    
}
 */