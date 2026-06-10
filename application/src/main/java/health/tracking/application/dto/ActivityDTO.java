package health.tracking.application.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {
    private Long id;
    private String activityType;
    @Positive(message = "Durata trebuie sa fie pozitiva")
    private int duration;
    @PastOrPresent(message = "Data nu poate fi in viitor")
    private LocalDate activityDate;
    @Positive(message = "Caloriile nu pot fi negative")
    private int calories;

}
