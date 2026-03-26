package health.tracking.application.mapper;


import health.tracking.application.dto.MessageDTO;
import health.tracking.application.dto.PostRequestDTO;
import health.tracking.application.dto.PostResponseDTO;
import health.tracking.application.entities.Message;
import health.tracking.application.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "sender.username", target = "sender")

    MessageDTO toDto(Message message);


    @Mapping(target = "sender", ignore = true)
    Message toEntity(MessageDTO dto);
}