package health.tracking.application.service;


import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.dto.UserStreakRequestDTO;
import health.tracking.application.dto.UserStreakResponseDTO;
import health.tracking.application.entities.*;
import health.tracking.application.mapper.UserStreakMapper;
import health.tracking.application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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

    @Autowired
    GoalLogRepository goalLogRepository;

    @Autowired
    GoalRepository goalRepository;


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


    @Scheduled(cron = "0 59 23 * * *")
    public void finalizeStreak(){ //functie care se va apela mereu la ora 23:59
        LocalDate today=LocalDate.now();

        List<UserStreak> activeStreak=userStreakRepository.findAll();

        for(UserStreak streak: activeStreak){
            if(!streak.getLastStreakDate().equals(today)){
                User u1=streak.getUserOne();
                User u2=streak.getUserTwo();
                if(bothCloseGoals(u1,u2, today)){
                    streak.setStreakCount(streak.getStreakCount()+1);
                    streak.setLastStreakDate(today);
                    userStreakRepository.save(streak);
                }
                else{
                    streak.setStreakCount(0);
                    userStreakRepository.save(streak);
                }
            }
        }

    }

    private boolean bothCloseGoals(User u1, User u2, LocalDate date) {
        return checkUserGoals(u1, date) && checkUserGoals(u2, date);
    }

    private boolean checkUserGoals(User u, LocalDate date) {

        List<GoalLog> logs = goalLogRepository.findByUserAndDate(u, date);

        if (logs.size() < 3) return false;

        return logs.stream().allMatch(GoalLog::isCompleted);
    }



    public int getStreakBetweenUsers(String sender, String receiver) {
        User u1 = userRepository.findByEmailOrUsername(sender, sender);
        User u2 = userRepository.findByEmailOrUsername(receiver, receiver);
        UserStreak us = userStreakRepository.findStreakBetweenUsers(u1, u2);

        if (us != null) {
            if (us.getLastStreakDate().isBefore(LocalDate.now().minusDays(1))) {
                us.setStreakCount(0);
                userStreakRepository.save(us);
                return 0;
            }
            return us.getStreakCount();
        }
        return 0;
    }



}




