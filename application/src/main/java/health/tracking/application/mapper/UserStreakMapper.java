package health.tracking.application.mapper;

import health.tracking.application.dto.GoalDTO;
import health.tracking.application.dto.UserStreakRequestDTO;
import health.tracking.application.dto.UserStreakResponseDTO;
import health.tracking.application.entities.Goal;
import health.tracking.application.entities.UserStreak;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserStreakMapper {


    UserStreakResponseDTO toDto(UserStreak userStreak);

    UserStreak toEntity(UserStreakRequestDTO dto);

}
