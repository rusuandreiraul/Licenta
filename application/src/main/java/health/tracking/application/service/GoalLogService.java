package health.tracking.application.service;

import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.entities.Goal;
import health.tracking.application.entities.GoalLog;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.UserMapper;
import health.tracking.application.repository.GoalLogRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GoalLogService {

    @Autowired
    GoalLogRepository goalLogRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    public List<UserResponseDTO> getLeaders() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        // Preluare loguri din ultimele 30 de zile
        List<GoalLog> goalLogListMonth = goalLogRepository.findAllByDateBetween(startDate, endDate);

        return goalLogListMonth.stream()
                // Grupare dupa ID-ul utilizatorului
                .collect(Collectors.groupingBy(
                        log -> log.getUser().getId(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(3)
                .map(entry -> {
                    User user = goalLogListMonth.stream()
                            .map(GoalLog::getUser)
                            .filter(u -> u.getId().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);
                    return userMapper.toDto(user);
                })
                .collect(Collectors.toList());
    }

}
