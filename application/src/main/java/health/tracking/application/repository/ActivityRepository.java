package health.tracking.application.repository;

import health.tracking.application.dto.ActivityChartSeriesDTO;
import health.tracking.application.entities.Activity;
import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface ActivityRepository extends JpaRepository<Activity, Long> {


    List<Activity> findByUserAndActivityDate(User u, LocalDate dateRequest);

    @Query("""
        SELECT new health.tracking.application.dto.ActivityChartSeriesDTO(
            COALESCE(SUM(a.calories), 0),
            a.activityDate
        )
        FROM Activity a
        WHERE a.user.username = :username
        AND a.activityDate BETWEEN :startDate AND :endDate
        GROUP BY a.activityDate
        ORDER BY a.activityDate ASC
    """)
    List<ActivityChartSeriesDTO> findActivitiesByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("username") String username
    );


   List<Activity> findActivitiesByActivityDateAndUser(LocalDate d,User u);


    @Query("""
    SELECT COALESCE(SUM(a.calories), 0)
    FROM Activity a
    WHERE a.user = :user
    AND a.activityDate BETWEEN :startDate AND :endDate
    GROUP BY a.activityDate
    ORDER BY a.activityDate ASC
""")
    List<Integer> findCaloriesByUserAndDateRange(@Param("user") User u,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate d);
}

