package health.tracking.application.service;


import health.tracking.application.dto.UserStreakRequestDTO;
import health.tracking.application.dto.UserStreakResponseDTO;
import health.tracking.application.entities.Goal;
import health.tracking.application.entities.Sleep;
import health.tracking.application.entities.User;
import health.tracking.application.entities.UserStreak;
import health.tracking.application.mapper.UserStreakMapper;
import health.tracking.application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserStreakService {

    @Autowired
    UserStreakRepository userStreakRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    AlimentationRepository alimentationRepository;

    @Autowired
    SleepRepository sleepRepository;

    @Autowired
    UserStreakMapper userStreakMapper;


    public UserStreakResponseDTO addOrUpdateStreak(UserStreakRequestDTO dto) {
        User u1=userRepository.findByEmailOrUsername(dto.getReceiver(), dto.getReceiver());
        User u2=userRepository.findByEmailOrUsername(dto.getSender(), dto.getSender());

        if(u1==null || u2==null){
            return null;
        }

        UserStreak userStreak=userStreakRepository.findStreakBetweenUsers(u1,u2);
        if(userStreak!=null){
            if(bothCloseGoals(u1,u2, LocalDate.now()) && !userStreak.getLastStreakDate().equals(LocalDate.now())) {
                userStreak.setStreakCount(userStreak.getStreakCount() + 1);
                userStreak.setLastStreakDate(LocalDate.now());
                userStreakRepository.save(userStreak);
                return userStreakMapper.toDto(userStreak);
           }
        }
        if(bothCloseGoals(u1,u2,LocalDate.now())) {
            UserStreak newStreak = new UserStreak();
            newStreak.setUserOne(u1);
            newStreak.setUserTwo(u2);
            newStreak.setStreakCount(1);
            newStreak.setLastStreakDate(LocalDate.now());
            userStreakRepository.save(newStreak);
            return userStreakMapper.toDto(newStreak);
        }
        return null;
    }
 //aici verific daca ambii si-au facut obiectivele
 private boolean bothCloseGoals(User u1, User u2, LocalDate date) {

     int ex1 = activityRepository.findDurationByUserAndActivityDate(u1, date);
     int ex2 = activityRepository.findDurationByUserAndActivityDate(u2, date);

     double cal1 = alimentationRepository.findCaloriesByUserAndMealDate(u1, date);
     double cal2 = alimentationRepository.findCaloriesByUserAndMealDate(u2, date); // corectat u2 aici

     int slp1 = sleepRepository.findHoursSleptByUserAndDateSleep(u1, date);
     int slp2 = sleepRepository.findHoursSleptByUserAndDateSleep(u2, date);

     boolean u1Completed = checkUserGoals(u1, ex1, cal1, slp1);


     boolean u2Completed = checkUserGoals(u2, ex2, cal2, slp2);

     return u1Completed && u2Completed;
 }

    private boolean checkUserGoals(User user, int ex, double cal, int slp) {
        int completed = 0;
        for (Goal goal : user.getGoalList()) {
            if ("Activity".equalsIgnoreCase(goal.getType()) && ex >= goal.getTargetValue()) completed++;
            if ("Alimentation".equalsIgnoreCase(goal.getType()) && cal >= goal.getTargetValue()) completed++;
            if ("Sleep".equalsIgnoreCase(goal.getType()) && slp >= goal.getTargetValue()) completed++;
        }
        return completed >= 3;
    }
}




