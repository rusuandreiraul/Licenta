package health.tracking.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SleepDTO {
    private Long id;
    @Min(value = 0, message = "calitatea somnului trebuie sa fie intre valorile 0 si 5")
    @Max(value=5, message = "calitatea somnului trebuie sa fie intre valorile 0 si 5")
    private int quality;
    @PositiveOrZero(message = "Orele de somn trebuie sa fie pozitive sau 0")
    private int hoursSlept;
    private LocalDate dateSleep;
    @Min(value = 0, message = "stresul trebuie sa fie intre valorile 0 si 5")
    @Max(value=5, message = "stresul trebuie sa fie intre valorile 0 si 5")
    private int stress;
    @Min(value = 0, message = "energia de dimineata trebuie sa fie intre valorile 0 si 5")
    @Max(value=5, message = "energia de dimineata trebuie sa fie intre valorile 0 si 5")
    private int morningEnergy;

}



