package health.tracking.application.repository;

import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    User findByEmailOrUsername(String username, String username1);
}
