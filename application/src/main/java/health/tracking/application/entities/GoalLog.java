package health.tracking.application.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"goal_id", "user_id", "date"})
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class GoalLog {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="goal_id")
    private Goal goal;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int currentValue;

    private boolean isCompleted;

    private LocalDate date;

}
