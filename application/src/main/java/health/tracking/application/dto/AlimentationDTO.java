package health.tracking.application.dto;

import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlimentationDTO {
    private Long id;
    private String carbohydrates;
    private double calories;
    private String type;
    private String fat;
    private double proteins;
    @PastOrPresent(message = "Datele nu pot fi introduse pentru o data viitoare")
    private LocalDate mealDate;
    private String nameProduct;
}
