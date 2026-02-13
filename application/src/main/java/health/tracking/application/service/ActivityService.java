package health.tracking.application.service;

import health.tracking.application.dto.ActivityChartSeriesDTO;
import health.tracking.application.dto.ActivityDTO;
import health.tracking.application.entities.Activity;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.ActivityMapper;
import health.tracking.application.repository.ActivityRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityMapper activityMapper;

    public List<ActivityDTO> getActivityByDate(String username, String dateRequest) {
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            LocalDate date= LocalDate.parse(dateRequest);
            List<Activity> activities=activityRepository.findByUserAndActivityDate(u, date);

            return activities.stream()
                    .map(activityMapper::toDto)
                    .toList();

        }
        else{
            return null;
        }
    }

    public String addActivity(ActivityDTO activityDTO, String username, String date) {
        LocalDate d= LocalDate.parse(date);
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            Activity a=new Activity();
            a.setActivityDate(d);
            a.setActivityType(activityDTO.getActivityType());
            a.setUser(u);
            a.setCalories(activityDTO.getCalories());
            a.setDuration(activityDTO.getDuration());
            activityRepository.save(a);
            return "Activitate adaugata cu success!";
        }

        return "Eroare";

    }

    public List<ActivityChartSeriesDTO> getSeriesByDate(String username, String date) {
        User u = userRepository.findByEmailOrUsername(username, username);
        if (u != null) {
            LocalDate dateSelected = LocalDate.parse(date);
            LocalDate startDate = dateSelected.minusDays(6);
            return activityRepository.findActivitiesByDateRange(startDate, dateSelected, u.getUsername());
        }
        return new ArrayList<>();
    }
}
