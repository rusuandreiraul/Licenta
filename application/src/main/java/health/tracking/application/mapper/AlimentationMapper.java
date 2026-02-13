package health.tracking.application.mapper;

import health.tracking.application.dto.AlimentationDTO;
import health.tracking.application.dto.GoalDTO;
import health.tracking.application.entities.Alimentation;
import health.tracking.application.entities.Goal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlimentationMapper {

    AlimentationDTO toDto(Alimentation alimentation);

    Alimentation toEntity(AlimentationDTO dto);
}
