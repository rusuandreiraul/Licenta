package health.tracking.application.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Alimentation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private double calories;
    private String carbohydrates;
    private String fat;
    private double proteins;
    private LocalDate mealDate;
    private String nameProduct;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getNameProduct() {
        return this.nameProduct;
    }
}
