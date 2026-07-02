package health.tracking.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    @Email
    private String email;
    private String username;
    @Size(min = 8, message = "Parola trebuie să aibă o lungime minimă de 8 caractere")
    private String password;
    @Positive(message = "Inalțimea trebuie să fie pozitivă")
    private double height;
    @Positive(message = "Greutatea trebuie să fie pozitivă")
    private double weight;
    private MultipartFile profileImage;
    @PastOrPresent(message = "Data nașterii nu poate fi în viitor")
    private LocalDate birthDate;
}




