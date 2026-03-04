package health.tracking.application.mapper;



import health.tracking.application.dto.PostRequestDTO;
import health.tracking.application.dto.PostResponseDTO;
import health.tracking.application.entities.Post;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {


     Post toEntity(PostRequestDTO dto);

    PostResponseDTO toDto(Post post);
}
