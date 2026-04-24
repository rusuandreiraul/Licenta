package health.tracking.application.service;

import health.tracking.application.controller.SleepController;
import health.tracking.application.dto.SleepDTO;
import health.tracking.application.entities.Goal;
import health.tracking.application.entities.GoalLog;
import health.tracking.application.entities.Sleep;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.SleepMapper;
import health.tracking.application.repository.GoalLogRepository;
import health.tracking.application.repository.GoalRepository;
import health.tracking.application.repository.SleepRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SleepService {

    @Autowired
    SleepRepository sleepRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SleepMapper sleepMapper;

    @Autowired
    GoalRepository goalRepository;

    @Autowired
    GoalLogRepository goalLogRepository;

    public List<SleepDTO> findSleepWeekByDate(String selectedDate, String username) {
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            LocalDate d1= LocalDate.parse(selectedDate);
            LocalDate d2=d1.minusDays(6);
            return sleepRepository.findSleepByDateRange(d2, d1, u.getUsername());
        }
        else{
            return new ArrayList<>();
        }

    }

    @Transactional
    public String saveSleep(SleepDTO s, String username, String selectedDate) {
        LocalDate date = LocalDate.parse(selectedDate);
        User u = userRepository.findByEmailOrUsername(username, username);

        if (u == null) {
            return "Eroare: Utilizatorul nu a fost găsit!";
        }

        Sleep sleep = new Sleep();
        sleep.setQuality(s.getQuality());
        sleep.setHoursSlept(s.getHoursSlept());
        sleep.setDateSleep(date);
        sleep.setUser(u);
        sleepRepository.save(sleep);

        Goal g = goalRepository.findByUserAndType(u, "Sleep");
        if (g != null) {
            GoalLog glog = goalLogRepository.findByUserAndGoalAndDate(u, g, date);

            if (glog == null) {

                glog = new GoalLog();
                glog.setUser(u);
                glog.setGoal(g);
                glog.setDate(date);
                glog.setCurrentValue(s.getHoursSlept());
            } else {
                glog.setCurrentValue(glog.getCurrentValue() + s.getHoursSlept());
            }

            glog.setCompleted(glog.getCurrentValue() >= g.getTargetValue());

            goalLogRepository.save(glog);
        }

        return "Orele de somn și progresul obiectivului au fost salvate cu succes!";
    }

    public SleepDTO findSleepByDate(String username, String selectedDate) {
        User u=userRepository.findByEmailOrUsername(username,username);
        LocalDate date= LocalDate.parse(selectedDate);
        if(u!=null){
            Sleep s=sleepRepository.findByUserAndDateSleep(u, date);
            if(s!=null){
                return sleepMapper.toDto(s);
            }
            return null;
        }
        return null;
    }
}
