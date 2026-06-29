package health.tracking.application.service;

import health.tracking.application.entities.PasswordReset;
import health.tracking.application.entities.User;
import health.tracking.application.repository.PasswordResetRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private JavaMailSender mailSender;

    public String passwordReset(String email){
        User u=userRepository.findByEmailOrUsername(email, email);

        if(u==null){
            return "Utilizatorul nu există";
        }

        String token= UUID.randomUUID().toString();

        PasswordReset reset=new PasswordReset();
        reset.setToken(token);
        reset.setUser(u);
        reset.setExpireTime(LocalDateTime.now().plusMinutes(15));
        passwordResetRepository.save(reset);

        String urlReset="http://localhost:3000/reset-password?token=" + token;
        sendEmail(u.getEmail(), urlReset);
        return "Email trimis cu succes!";
    }

    private void sendEmail(String email, String link){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Resetare parolă - WellSync");
        message.setText("Salut, \n\nApasă pe linkul de mai jos pentru a reseta parola. Link valabil 15 minute:\n"+link+"\n\nDacă nu tu ai cerut asta, ignoră email-ul");


        mailSender.send(message);

    }
}
