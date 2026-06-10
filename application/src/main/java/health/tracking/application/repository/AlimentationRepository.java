package health.tracking.application.repository;

import health.tracking.application.entities.Alimentation;
import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AlimentationRepository extends JpaRepository<Alimentation, Long> {


    List<Alimentation> findByUserAndMealDate(User u, LocalDate d);

    double findCaloriesByUserAndMealDate(User u1, LocalDate date);



    List<Alimentation> findAllByUserAndMealDateAfter(User user, LocalDate threeDaysAgo);
}
