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

        // 2. Preluare loguri și filtrare (Direct din bază dacă e posibil, sau prin Stream)
        List<GoalLog> goalLogListMonth = goalLogRepository.findAll().stream()
                .filter(g -> g.getDate().isAfter(startDate) && g.getDate().isBefore(endDate))
                .toList();

        // 3. Gruparea și numărarea obiectivelor per utilizator
        return goalLogListMonth.stream()
                .collect(Collectors.groupingBy(
                        GoalLog::getUser,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())

                .limit(3)
                .map(entry -> userMapper.toDto(entry.getKey()))
                .collect(Collectors.toList());
    }
}
