package health.tracking.application.service;


import health.tracking.application.dto.UserStreakRequestDTO;
import health.tracking.application.dto.UserStreakResponseDTO;
import health.tracking.application.entities.User;
import health.tracking.application.entities.UserStreak;
import health.tracking.application.mapper.UserStreakMapper;
import health.tracking.application.repository.UserRepository;
import health.tracking.application.repository.UserStreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserStreakService {

    @Autowired
    UserStreakRepository userStreakRepository;

    @Autowired
    UserRepository userRepository;

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
            if(bothCloseGoals(u1,u2)) {
                userStreak.setStreakCount(userStreak.getStreakCount() + 1);
                userStreakRepository.save(userStreak);
                return userStreakMapper.toDto(userStreak);
            }
        }
        UserStreak newStreak=new UserStreak();
        newStreak.setUserOne(u1);
        newStreak.setUserTwo(u2);
        newStreak.setStreakCount(1);
        newStreak.setLastStreakDate(LocalDate.now());
        userStreakRepository.save(newStreak);
        return userStreakMapper.toDto(newStreak);
    }
 //aici verific daca ambii si-au facut obiectivele
    //private boolean bothCloseGoals(User u1, User u2) {

    //}
}




