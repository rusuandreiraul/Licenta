package health.tracking.application.repository;

import health.tracking.application.entities.Goal;
import health.tracking.application.entities.GoalLog;
import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GoalLogRepository extends JpaRepository<GoalLog,Long> {


    GoalLog findByUserAndGoalAndDate(User u, Goal g, LocalDate date);



    List<GoalLog> findByUserAndDate(User u, LocalDate date);
}
