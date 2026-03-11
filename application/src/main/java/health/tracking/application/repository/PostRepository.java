package health.tracking.application.repository;

import health.tracking.application.dto.PostResponseDTO;
import health.tracking.application.entities.Post;
import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {


    List<Post> findAllByPublishDateAndUserIn(LocalDate now, Set<User> follow);

    List<Post> findAllByUser(User u);
}
