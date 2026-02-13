package health.tracking.application.controller;

import health.tracking.application.dto.UserRequestDTO;
import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO dto) {
        UserResponseDTO response = userService.register(dto);
        if (response != null)
            return ResponseEntity.ok(response);
        else
            return ResponseEntity.badRequest().body("Date invalide. Verifică valorile introduse!");

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials){
        UserResponseDTO dto=userService.login(credentials);
        if(dto!=null){
            return ResponseEntity.ok(dto);
        }
        else{
            return ResponseEntity.badRequest().body("Credentiale gresite");
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?>getUser(@PathVariable String username){
        UserResponseDTO dto=userService.getUser(username);
        if(dto!=null){
            return ResponseEntity.ok(dto);
        }
        else{
            return ResponseEntity.badRequest().body("Userul nu a fost returnat cu success");
        }
    }

}
