package health.tracking.application.mapper;

import health.tracking.application.dto.SleepDTO;
import health.tracking.application.entities.Sleep;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SleepMapper {
    Sleep toEntity(SleepDTO dto);

    SleepDTO toDto(Sleep sleep);
}
