package health.tracking.application.service;

import health.tracking.application.dto.DashboardDailyDTO;
import health.tracking.application.dto.DashboardWeekDTO;
import health.tracking.application.entities.Activity;
import health.tracking.application.entities.Sleep;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.ActivityMapper;
import health.tracking.application.mapper.SleepMapper;
import health.tracking.application.repository.ActivityRepository;
import health.tracking.application.repository.SleepRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    SleepRepository sleepRepository;

    @Autowired
    ActivityMapper activityMapper;
    @Autowired
    SleepMapper sleepMapper;

    public DashboardDailyDTO getDailyData(String username, String selectedDate) {
        int totalDuration=0;

        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            LocalDate d= LocalDate.parse(selectedDate);
            DashboardDailyDTO dailyDTO=new DashboardDailyDTO();
            List<Activity> activityList= activityRepository.findActivitiesByActivityDateAndUser(d,u);
            dailyDTO.setActivityDetails(activityList.stream().map(activityMapper::toDto).toList());
            for(Activity a:activityList){
                totalDuration+=a.getDuration();
            }
            dailyDTO.setTotalActivityDuration(totalDuration);

            Sleep s=sleepRepository.findSleepByDateSleepAndUser(d,u);
            if(s!=null) {
                dailyDTO.setSleepDetails(sleepMapper.toDto(s));
                dailyDTO.setTotalHoursSleep(s.getHoursSlept());
            }
            else{
                dailyDTO.setSleepDetails(null);
                dailyDTO.setTotalHoursSleep(0);
            }
            return dailyDTO;
        }
        return null;
    }



    public DashboardWeekDTO getWeekData(String username, String selectedDate) {
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            DashboardWeekDTO weekData=new DashboardWeekDTO();
            LocalDate d=LocalDate.parse(selectedDate);
            LocalDate startDate=d.minusDays(6);
            List<Integer> calories=activityRepository.findCaloriesByUserAndDateRange(u, startDate, d);
            List<Integer> qualitySleep=sleepRepository.findQualityByUserAndDateRange(u, startDate, d);
            weekData.setCalories(calories);
            weekData.setQualitySleep(qualitySleep);
            weekData.setCaloriesConsumed(List.of(1230,120,100,230,200,1000,0));
            return weekData;
        }
        return null;
    }
}
