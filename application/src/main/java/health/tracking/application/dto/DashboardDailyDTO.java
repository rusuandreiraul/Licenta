package health.tracking.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDailyDTO {
     int totalActivityDuration;
     int totalHoursSleep;
     //int totalCaloriesConsumed;
     List<ActivityDTO> activityDetails;
     SleepDTO sleepDetails;
    // List<AlimentationDTO> alimentationDetails;


}
