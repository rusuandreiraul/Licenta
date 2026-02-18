package health.tracking.application.entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.NotFound;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "sleep", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date_sleep"})
}) //nu permite sa exsite mai multe sleepuri pentru aceeasi data cu acelasi user
public class Sleep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private int hoursSlept;
    private LocalDate dateSleep;
    private int quality;
    private int stress;
    private int morningEnergy;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
