package sn.estm.managingrestauranttickets.mappers;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.estm.managingrestauranttickets.dto.AccountDTO;
import sn.estm.managingrestauranttickets.entities.Account;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AccountMapper {

    @Mapping(source = "user", target = "userDTO")
    AccountDTO toDto(Account account);

    @Mapping(target = "user", ignore = true) // Ignorer le mapping de l'utilisateur pour éviter les problèmes de récursion infinie
    Account toEntity(AccountDTO accountDTO);

    Set<AccountDTO> toDtoSet(Set<Account> accounts);

    Set<Account> toEntitySet(Set<AccountDTO> accountDTOs);

}

