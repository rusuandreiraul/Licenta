package health.tracking.application.mapper;

import health.tracking.application.dto.ActivityDTO;
import health.tracking.application.entities.Activity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityDTO toDto(Activity activity);

    Activity toEntity(ActivityDTO dto);
}
