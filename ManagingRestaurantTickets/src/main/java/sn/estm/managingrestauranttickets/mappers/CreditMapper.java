/* Mapper for CreditDTO and Credit entities */
package sn.estm.managingrestauranttickets.mappers;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.estm.managingrestauranttickets.dto.CreditDTO;
import sn.estm.managingrestauranttickets.entities.Credit;


@Mapper(componentModel = "spring", uses = {AccountMapper.class, UserMapper.class})
public interface CreditMapper {

    @Mapping(source = "account", target = "accountDTO")
    @Mapping(source = "user", target = "userDTO")
    CreditDTO toDto(Credit credit);

    @Mapping(target = "account", ignore = true) // Ignorer le mapping du compte pour éviter les problèmes de récursion infinie
    @Mapping(target = "user", ignore = true) // Ignorer le mapping de l'utilisateur pour éviter les problèmes de récursion infinie
    Credit toEntity(CreditDTO creditDTO);

    Set<CreditDTO> toDtoSet(Set<Credit> credits);

    Set<Credit> toEntitySet(Set<CreditDTO> creditDTOs);
}
