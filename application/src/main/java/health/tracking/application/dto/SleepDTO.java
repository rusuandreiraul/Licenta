package health.tracking.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
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
    @Min(value = 0, message = "Calitatea somnului trebuie să fie între valorile 0 si 5")
    @Max(value=5, message = "Calitatea somnului trebuie să fie între valorile 0 si 5")
    private int quality;
    @PositiveOrZero(message = "Orele de somn trebuie să fie pozitive sau 0")
    private int hoursSlept;
    @PastOrPresent(message = "Datele despre somn nu pot fi introduse pentru o dată viitoare")
    private LocalDate dateSleep;
    @Min(value = 0, message = "Stresul trebuie să fie între valorile 0 si 5")
    @Max(value=5, message = "Stresul trebuie să fie între valorile 0 si 5")
    private int stress;
    @Min(value = 0, message = "Energia de dimineață trebuie să fie între valorile 0 si 5")
    @Max(value=5, message = "Energia de dimineață trebuie să fie între valorile 0 si 5")
    private int morningEnergy;

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public int getMorningEnergy() {
        return morningEnergy;
    }

    public void setMorningEnergy(int morningEnergy) {
        this.morningEnergy = morningEnergy;
    }

}



