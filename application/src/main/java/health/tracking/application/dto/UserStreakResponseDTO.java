package health.tracking.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserStreakResponseDTO {
    private String sender;
    private String receiver;
    private int counter;
}
