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
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    private CloudinaryService cloudinaryService;

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

        if(dto.getProfileImage()!=null && !dto.getProfileImage().isEmpty()){
            String imageUrl= cloudinaryService.uploadImage(dto.getProfileImage());

            user.setUrlProfileImage(imageUrl);
        }


        String encodedPassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }


    public UserResponseDTO getUser(String username) {
        User u= userRepository.findByEmailOrUsername(username,username);
        return userMapper.toDto(u);
    }


    public String followUser(String loggedUser,String username) {
        if(Objects.equals(loggedUser, username)){
            return "Eroare";
        }

        User logUser=userRepository.findByEmailOrUsername(loggedUser, loggedUser);
        User user=userRepository.findByEmailOrUsername(username,username);

        if(logUser.getFollowing().contains(user)){
            logUser.getFollowing().remove(user);
            userRepository.save(logUser);
            return "Unfollow";
        }
        else{
            logUser.getFollowing().add(user);
            userRepository.save(logUser);
            return "Followed";
        }




    }

    public Boolean checkStatus(String loggedUser, String username) {
        User logUser=userRepository.findByEmailOrUsername(loggedUser, loggedUser);
        User user=userRepository.findByEmailOrUsername(username,username);

        if(logUser.getFollowing().contains(user)){
            return true;
        }
        else{
            return false;
        }
    }

    public UserResponseDTO updateUser(UserRequestDTO dto,String username) {
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            u.setHeight(dto.getHeight());
            u.setWeight(dto.getWeight());
            if(dto.getProfileImage()!=null && !dto.getProfileImage().isEmpty()){
                String imageUrl= cloudinaryService.uploadImage(dto.getProfileImage());

                u.setUrlProfileImage(imageUrl);
            }
            User savedUser=userRepository.save(u); //update pe user
            return userMapper.toDto(u);
        }
        return null;

    }
}
