package health.tracking.application.repository;

import health.tracking.application.entities.Alimentation;
import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AlimentationRepository extends JpaRepository<Alimentation, Long> {


    List<Alimentation> findByUserAndMealDate(User u, LocalDate d);

    double findCaloriesByUserAndMealDate(User u1, LocalDate date);


    List<Alimentation> findAllByUserAndMealDateAfter(User user, LocalDate threeDaysAgo);


    @Query("""
    SELECT a.calories
    FROM Alimentation a
    WHERE a.user = :user
    AND a.mealDate BETWEEN :startDate AND :endDate
    ORDER BY a.mealDate ASC
""")
    List<Integer> findCaloriesByUserAndDateRange(@Param("user") User u,@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate d);


    @Query("""
    SELECT al 
    FROM Alimentation al
    WHERE al.user = :user
    AND al.mealDate BETWEEN :startDate AND :endDate
    ORDER BY al.mealDate ASC
""")
    List<Alimentation> findByUserAndDateRange(
            @Param("user") User u,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
