package health.tracking.application.repository;

import health.tracking.application.entities.User;
import health.tracking.application.entities.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserStreakRepository extends JpaRepository<UserStreak,Long> {

    @Query("SELECT s FROM UserStreak s WHERE (s.userOne = :u1 AND s.userTwo = :u2) OR (s.userOne = :u2 AND s.userTwo = :u1)")
    UserStreak findStreakBetweenUsers(@Param("u1") User u1, @Param("u2") User u2);
}
