package health.tracking.application.service;

import health.tracking.application.dto.UserRequestDTO;
import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.UserMapper;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponseDTO register(UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        User existent=userRepository.findByEmail(dto.getEmail());
        if (existent != null) {
            throw new IllegalArgumentException("Emailul este deja folosit.");
        }

        if (user.getHeight() <= 0 || user.getWeight() <= 0) {
            throw new IllegalArgumentException("Înălțimea și greutatea trebuie să fie pozitive.");
        }

        if (user.getBirthDate() == null || !user.getBirthDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data nașterii trebuie să fie în trecut.");
        }

        String encodedPassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    public UserResponseDTO login(Map<String, String> credentials) {
        String username=credentials.get("username");
        String password=credentials.get("password");
        UserResponseDTO dto;
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null && passwordEncoder.matches(password, u.getPassword())){
            dto=userMapper.toDto(u);
        }
        else{
            return null;
        }
        return dto;
    }

    public UserResponseDTO getUser(String username) {
        User u= userRepository.findByEmailOrUsername(username,username);


        return userMapper.toDto(u);
    }
}
