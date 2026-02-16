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
     double totalCaloriesConsumed;
     double totalCarbosConsumed;
     double totalFatConsumed;
     double totalProteinConsumed;
     List<ActivityDTO> activityDetails;
     SleepDTO sleepDetails;
     List<String> alimentationName;


}
