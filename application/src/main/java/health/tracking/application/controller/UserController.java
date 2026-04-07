package health.tracking.application.controller;

import health.tracking.application.dto.LoginResponseDTO;
import health.tracking.application.dto.UserRequestDTO;
import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.service.JwtService;
import health.tracking.application.service.UserService;
import lombok.Getter;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO dto) {
        UserResponseDTO response = userService.register(dto);
        if (response != null)
            return ResponseEntity.ok(response);
        else
            return ResponseEntity.badRequest().body("Date invalide. Verifică valorile introduse!");

    }


    @GetMapping("/search-user/{username}")
    public UserResponseDTO searchUser(@PathVariable String username){
        UserResponseDTO response=userService.getUser(username);
        if(response !=null){
            return response;
        }
        else{
            return null;
        }
    }

    //public ResponseEntity<?> changeUser(@RequestBody UserRequestDTO dto){

    //}

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody Map<String, String> credentials){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.get("username"), credentials.get("password"))
        );

        LoginResponseDTO dto=new LoginResponseDTO();
        dto.setToken(jwtService.generateJWT(credentials.get("username")));
        dto.setUsername(credentials.get("username"));

        return dto;
    }

    @GetMapping("/profile")
    public ResponseEntity<?>getUser(Authentication authentication){
        String username=authentication.getName();
        UserResponseDTO dto=userService.getUser(username);
        if(dto!=null){
            return ResponseEntity.ok(dto);
        }
        else{
            return ResponseEntity.badRequest().body("Userul nu a fost returnat cu success");
        }
    }

    @PostMapping("/user-change")
    public ResponseEntity<?>changeUser(@RequestBody UserResponseDTO dto, Authentication authentication){
        String username=authentication.getName();

        UserResponseDTO updatedDto = userService.updateUser(dto, username);

        if (updatedDto != null) {
            return ResponseEntity.ok(updatedDto);
        }


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Eroare - Utilizatorul nu a putul fi actualizat");

    }

    //FOLLOW USER

    @PutMapping("/follow-user/{username}")
    public ResponseEntity<?> followUser(@PathVariable String username, Authentication authentication){
        String loggedUser=authentication.getName(); //luam userul din tokenul salvat   UsernamePasswordAuthenticationToken asta e implementearea pentru authenticaion care e interfata
        String response=userService.followUser(loggedUser, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-follow/{username}")
    public ResponseEntity<Boolean> checkFollow(@PathVariable String username, Authentication authentication){
        String loggedUser=authentication.getName();
        Boolean status=userService.checkStatus(loggedUser, username);
        return ResponseEntity.ok(status);
    }

}
