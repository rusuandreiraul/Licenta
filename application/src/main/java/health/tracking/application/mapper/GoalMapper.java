package health.tracking.application.mapper;

import health.tracking.application.dto.ActivityDTO;
import health.tracking.application.dto.GoalDTO;
import health.tracking.application.entities.Activity;
import health.tracking.application.entities.Goal;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface GoalMapper {
    GoalDTO toDto(Goal goal);

    Goal toEntity(GoalDTO dto);
}
