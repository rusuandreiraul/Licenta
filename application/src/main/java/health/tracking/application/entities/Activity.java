package health.tracking.application.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String activityType;
    private int duration;
    private LocalDate activityDate;
    private int calories;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
