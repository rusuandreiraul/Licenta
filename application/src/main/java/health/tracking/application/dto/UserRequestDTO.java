package health.tracking.application.dto;

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
    private String email;
    private String username;
    private String password;
    private double height;
    private double weight;
    private MultipartFile profileImage;
    private LocalDate birthDate;


}
