package health.tracking.application.repository;

import health.tracking.application.entities.Conversation;
import health.tracking.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {


    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.sender.username = :u1 AND c.receiver.username = :u2) OR " +
            "(c.sender.username = :u2 AND c.receiver.username = :u1)")
    Optional<Conversation> findConversationBetweenUsers(@Param("u1") String u1,@Param("u2") String username1);
}
