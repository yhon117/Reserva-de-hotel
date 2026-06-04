package com.protec.recervhotel.mappers;

import com.protec.recervhotel.dto.UsuarioDTO;
import com.protec.recervhotel.dto.UsuarioRegistroDTO;
import com.protec.recervhotel.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioDTO toDto(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    Usuario toEntity(UsuarioRegistroDTO dto);

    List<UsuarioDTO> toListDto(List<Usuario> usuarios);
}
