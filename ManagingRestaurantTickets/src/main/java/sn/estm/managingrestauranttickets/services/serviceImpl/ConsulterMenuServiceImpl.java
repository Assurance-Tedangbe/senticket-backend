package sn.estm.managingrestauranttickets.services.serviceImpl;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn.estm.managingrestauranttickets.dto.ConsulterMenuDTO;
import sn.estm.managingrestauranttickets.entities.ConsulterMenu;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.ConsulterMenuMapper;
import sn.estm.managingrestauranttickets.repositories.ConsulterMenuRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.ConsulterMenuService;


@Slf4j
@Service
@RequiredArgsConstructor
public class ConsulterMenuServiceImpl implements ConsulterMenuService{
    
    private final ConsulterMenuRepository consulterMenuRepository;
    private final ConsulterMenuMapper consulterMenuMapper;

    @Override
    public ConsulterMenuDTO createConsulterMenu(ConsulterMenuDTO consulterMenuDTO) {
       
        log.info("Creating consulterMenu with details: {}", consulterMenuDTO);
        
        ConsulterMenu consulterMenu = consulterMenuMapper.toEntity(consulterMenuDTO);
        
        ConsulterMenu savedConsulterMenu = consulterMenuRepository.save(consulterMenu);

        log.info("ConsulterMenu created successfully with ID: {}",
         savedConsulterMenu.getConsulterMenuId());
       
        return consulterMenuMapper.toDto(savedConsulterMenu);
    }
 
    @Override
    public List<ConsulterMenuDTO> readConsulterMenus() {

        List<ConsulterMenu> consulterMenus = consulterMenuRepository.findAll();
        
        return consulterMenus.stream()
                .map(consulterMenuMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public ConsulterMenuDTO readConsulterMenuById(Long consulterMenuId) {
     
        log.info("Reading consulterMenu by id: {}", consulterMenuId);

        ConsulterMenu consulterMenu = consulterMenuRepository.findById(consulterMenuId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}", consulterMenuId)));

        return consulterMenuMapper.toDto(consulterMenu);
    }


    @Override
    public ConsulterMenuDTO updateConsulterMenu(ConsulterMenuDTO consulterMenuDTO) {
        
        log.info("Updating consulterMenu details: {}", consulterMenuDTO);

        ConsulterMenu existingConsulterMenu = consulterMenuRepository.findById(
                consulterMenuDTO.getConsulterMenuId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}",consulterMenuDTO.getConsulterMenuId())));
                    
        existingConsulterMenu.setConsultationDate(consulterMenuDTO.getConsultationDate());
        existingConsulterMenu.setUser(consulterMenuMapper.toEntity(consulterMenuDTO).getUser());
        existingConsulterMenu.setMenu(consulterMenuMapper.toEntity(consulterMenuDTO).getMenu());
        
        ConsulterMenu updatedConsulterMenu = consulterMenuRepository.save(existingConsulterMenu);

        log.info("ConsulterMenu updated successfully with date: {}", 
        updatedConsulterMenu.getConsultationDate());
        
        return consulterMenuMapper.toDto(updatedConsulterMenu);
    }


    @Override
    public void deleteConsulterMenu(Long consulterMenuId) {
     
        log.info("Deleting consulterMenu with id: {}", consulterMenuId);

        ConsulterMenu consulterMenu = consulterMenuRepository.findById(consulterMenuId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}", consulterMenuId)));
                    
        consulterMenuRepository.delete(consulterMenu);

        log.info("deleteConsulterMenu end ok - id: {}", consulterMenuId);
    }
}
