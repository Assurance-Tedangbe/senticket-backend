/* Mapper for DebitDTO and Debit entities */
package sn.estm.managingrestauranttickets.mappers;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.estm.managingrestauranttickets.dto.DebitDTO;
import sn.estm.managingrestauranttickets.entities.Debit;


@Mapper(componentModel = "spring", uses = {UserMapper.class, AccountMapper.class})
public interface DebitMapper {

    @Mapping(source = "account", target = "accountDTO")
    @Mapping(source = "user", target = "userDTO")
    DebitDTO toDto(Debit debit);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "user", ignore = true)
    Debit toEntity(DebitDTO debitDTO);

    Set<DebitDTO> toDtoSet(Set<Debit> debits);
    
    Set<Debit> toEntitySet(Set<DebitDTO> debitDTOs);
}
