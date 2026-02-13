package health.tracking.application.repository;

import health.tracking.application.dto.SleepDTO;
import health.tracking.application.entities.Sleep;
import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

public interface SleepRepository extends JpaRepository<Sleep, Long> {


    @Query("""
        SELECT new health.tracking.application.dto.SleepDTO(
             s.quality,
            s.hoursSlept,
            s.dateSleep
           
           )
        FROM Sleep s
        WHERE s.user.username = :username
        AND s.dateSleep BETWEEN :d2 AND :d1
        ORDER BY s.dateSleep ASC
    """)
    List<SleepDTO> findSleepByDateRange(@Param("d2") LocalDate d2, @Param("d1") LocalDate d1, @Param("username") String username);

    Sleep findByUserAndDateSleep(User u, LocalDate date);

    Sleep findSleepByDateSleepAndUser(LocalDate d,User u);


    @Query("""
    SELECT s.quality
    FROM Sleep s
    WHERE s.user = :user
    AND s.dateSleep BETWEEN :startDate AND :endDate
    ORDER BY s.dateSleep ASC
""")
    List<Integer> findQualityByUserAndDateRange(@Param("user") User u,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate d);
}
