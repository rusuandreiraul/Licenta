package health.tracking.application.dto;

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

    private String carbohydrates;
    private double calories;
    private String type;
    private String fat;
    private double proteins;
    private LocalDate mealDate;
    private String nameProduct;
}
