package health.tracking.application.service;

import health.tracking.application.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Căutăm user-ul în DB (folosește metoda ta din repository)
        health.tracking.application.entities.User user = userRepository.findByEmailOrUsername(username, username);

        if (user == null) {
            throw new UsernameNotFoundException("Utilizatorul nu a fost găsit: " + username);
        }

        // Returnăm un obiect User din Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>() // Aici pui lista de roluri dacă ai
        );
    }
}
