package health.tracking.application.service;



import health.tracking.application.dto.ActivityDTO;
import health.tracking.application.entities.Activity;
import health.tracking.application.entities.Goal;
import health.tracking.application.entities.GoalLog;
import health.tracking.application.entities.User;
import health.tracking.application.repository.ActivityRepository;

import health.tracking.application.repository.GoalLogRepository;
import health.tracking.application.repository.GoalRepository;
import health.tracking.application.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalLogRepository goalLogRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void addActivity_ShouldCreateNewGoalLog_WhenGoalLogDoesNotExist() {
        // 1. ARRANGE (Pregătirea datelor și a simulărilor)
        String username = "andrei.rusu";
        String dateStr = "2026-06-02";
        LocalDate localDate = LocalDate.parse(dateStr);

        ActivityDTO dto = new ActivityDTO();
        dto.setActivityType("Alergare");
        dto.setCalories(250);
        dto.setDuration(30); // 30 de minute

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setEmail(username);

        Goal mockGoal = new Goal();
        mockGoal.setType("Activity");
        mockGoal.setTargetValue(45);

        // Definirea regulilor pentru Mocks
        Mockito.when(userRepository.findByEmailOrUsername(username, username)).thenReturn(mockUser);
        Mockito.when(activityRepository.save(any(Activity.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(goalRepository.findByUserAndType(mockUser, "Activity")).thenReturn(mockGoal);

        // Simulam faptul că nu s-a găsit niciun GoalLog existent pentru această zi (returnează null)
        Mockito.when(goalLogRepository.findByUserAndGoalAndDate(mockUser, mockGoal, localDate)).thenReturn(null);
        Mockito.when(goalLogRepository.save(any(GoalLog.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT (Executarea metodei din Service)
        String response = activityService.addActivity(dto, username, dateStr);

        // 3. ASSERT (Verificarea rezultatelor brute și a comportamentului)
        assertEquals("Activitate și GoalLog adăugate cu succes!", response);

        // Verificăm riguros că Mockito a apelat salvările cu parametrii corecți în spate
        // De exemplu, verificăm dacă noul GoalLog a primit valoarea de 30 de minute și e setat pe false (30 < 45 target)
        verify(activityRepository, times(1)).save(any(Activity.class));
        verify(goalLogRepository, times(1)).save(argThat(glog ->
                glog.getCurrentValue() == 30 &&
                        !glog.isCompleted() &&
                        glog.getGoal() == mockGoal
        ));
    }
}
