package health.tracking.application.mapper;

import health.tracking.application.dto.UserRequestDTO;
import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class); //injectare nu prin spring ca nu merge, rezolva
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toDto(User user);
}
