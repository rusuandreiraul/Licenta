package health.tracking.application.service;

import health.tracking.application.controller.SleepController;
import health.tracking.application.dto.SleepDTO;
import health.tracking.application.entities.Sleep;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.SleepMapper;
import health.tracking.application.repository.SleepRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public String saveSleep(SleepDTO s, String username, String selectedDate) {

        User u=userRepository.findByEmailOrUsername(username, username);
        LocalDate date=LocalDate.parse(selectedDate);

        if(u!=null){
            Sleep sleep=new Sleep();
            sleep.setQuality(s.getQuality());
            sleep.setHoursSlept(s.getHoursSlept());
            sleep.setDateSleep(date);
            sleep.setUser(u);
            sleepRepository.save(sleep);
            return "Orele de somn au fost adaugate in sistem cu succes!";
        }
        return "Eroare";

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
