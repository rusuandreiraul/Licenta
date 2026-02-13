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
public class DashboardWeekDTO {
    List<Integer> calories;
    List<Integer>qualitySleep;
    List<Integer> caloriesConsumed;
}
