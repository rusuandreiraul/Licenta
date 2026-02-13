package health.tracking.application.repository;

import health.tracking.application.entities.Goal;
import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Goal findByUserAndType(User u, String type);

    List<Goal> findByUser(User u);
}
