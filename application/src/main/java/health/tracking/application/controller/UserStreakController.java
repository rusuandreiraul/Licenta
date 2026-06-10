package health.tracking.application.controller;


import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.dto.UserStreakRequestDTO;
import health.tracking.application.dto.UserStreakResponseDTO;
import health.tracking.application.entities.User;
import health.tracking.application.repository.UserRepository;
import health.tracking.application.service.UserStreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class UserStreakController {

    @Autowired
    private UserStreakService userStreakService;


    @PostMapping("/challenge")
    public ResponseEntity<?> challangeUser(@RequestBody UserStreakRequestDTO dto,Authentication authentication){
        String sender=authentication.getName();
        dto.setSender(sender);
        UserStreakResponseDTO response=userStreakService.addOrUpdateStreak(dto);
        if(response!=null){
            return ResponseEntity.ok(response);

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nu a fost posibila creare unui streak");
    }

    @GetMapping("/streak/{receiver}")
    public int getStreak(@PathVariable String receiver, Authentication authentication){
        String sender=authentication.getName();
        return userStreakService.getStreakBetweenUsers(sender, receiver);
    }

    @GetMapping("/best-streak")
    public ResponseEntity<UserStreakResponseDTO> getBestStreak(Authentication authentication){
        String username=authentication.getName();
        UserStreakResponseDTO dto=userStreakService.getBestStreak(username);
        System.out.println("DEBUG: dto de best streak este " + dto);
        return ResponseEntity.ok(userStreakService.getBestStreak(username));
    }


}
