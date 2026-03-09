package health.tracking.application.mapper;



import health.tracking.application.dto.PostRequestDTO;
import health.tracking.application.dto.PostResponseDTO;
import health.tracking.application.entities.Post;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {


     Post toEntity(PostRequestDTO dto);

    @Mapping(source = "user.username", target = "username")
    PostResponseDTO toDto(Post post);
}
