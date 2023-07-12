package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.entities.Menu;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.MenuService;

@Service
public class MenuServiceImpl implements MenuService{

    @Override
    public List<Menu> getAllMenus() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllMenus'");
    }

    @Override
    public void createMenu(Menu menu) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createMenu'");
    }

    @Override
    public Menu getMenuById(Long idMenu) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMenuById'");
    }

    @Override
    public void updateMenu(Long idMenu, Menu menu) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMenu'");
    }

    @Override
    public void deleteMenuById(Long idMenu) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteMenuById'");
    }
    
}
