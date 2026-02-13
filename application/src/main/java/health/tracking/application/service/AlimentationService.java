package health.tracking.application.service;

import health.tracking.application.dto.AlimentationDTO;
import health.tracking.application.entities.Activity;
import health.tracking.application.entities.Alimentation;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.AlimentationMapper;
import health.tracking.application.repository.AlimentationRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlimentationService {

    @Autowired
    AlimentationRepository alimentationRepository;

    @Autowired
    AlimentationMapper alimentationMapper;

    @Autowired
    UserRepository userRepository;

    public List<AlimentationDTO> findAlimentationDate(String username, String date) {
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            LocalDate d= LocalDate.parse(date);
            List<Alimentation> alimentation=alimentationRepository.findByUserAndMealDate(u, d);

            List<AlimentationDTO> dtos = alimentation.stream()
                    .map(alimentationMapper::toDto)
                    .toList();

            // LOG 2: Verifică DTO-ul după mapare
            dtos.forEach(dt -> System.out.println("DTO Name: " + dt.getNameProduct()));

            return dtos;

        }
        return null;
    }

    public String saveAlimentation(AlimentationDTO a, String username, String selectedDate) {
        User u=userRepository.findByEmailOrUsername(username, username);
        LocalDate d= LocalDate.parse(selectedDate);
        if(u!=null){
            Alimentation alimentation=new Alimentation();
            alimentation.setCalories(a.getCalories());
            alimentation.setNameProduct(a.getNameProduct());
            alimentation.setFat(a.getFat());
            alimentation.setType(a.getType());
            alimentation.setCarbohydrates(a.getCarbohydrates());
            alimentation.setUser(u);
            alimentation.setProteins(a.getProteins());
            alimentation.setMealDate(d);
            alimentation.setCalories(a.getCalories());
            alimentationRepository.save(alimentation);
            return "produs adaugat cu success!";
        }
        return "Eroare!";
    }
}
