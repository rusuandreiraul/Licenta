package health.tracking.application.service;

import health.tracking.application.dto.AlimentationDTO;
import health.tracking.application.entities.*;
import health.tracking.application.mapper.AlimentationMapper;
import health.tracking.application.repository.AlimentationRepository;
import health.tracking.application.repository.GoalLogRepository;
import health.tracking.application.repository.GoalRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    GoalLogRepository goalLogRepository;

    @Autowired
    GoalRepository goalRepository;

    public List<AlimentationDTO> findAlimentationDate(String username, String date) {
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            LocalDate d= LocalDate.parse(date);
            List<Alimentation> alimentation=alimentationRepository.findByUserAndMealDate(u, d);

            List<AlimentationDTO> dtos = alimentation.stream()
                    .map(alimentationMapper::toDto)
                    .toList();
            return dtos;

        }
        return null;
    }

    @Transactional
    public String saveAlimentation(AlimentationDTO a, String username, String selectedDate) {
        User u=userRepository.findByEmailOrUsername(username, username);
        LocalDate d= LocalDate.parse(selectedDate);


        if(u==null) return "Utilizator negasit!";


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

        Goal g=goalRepository.findByUserAndType(u, "Alimentation");
        if(g!=null){
            GoalLog glog=goalLogRepository.findByUserAndGoalAndDate(u,g,d);
            if (glog == null) {
                glog = new GoalLog();
                glog.setUser(u);
                glog.setGoal(g);
                glog.setDate(d);
                glog.setCurrentValue((int) a.getCalories());
            } else {
                glog.setCurrentValue((int) (glog.getCurrentValue() + a.getCalories()));
            }


            glog.setCompleted(glog.getCurrentValue() >= a.getCalories());

            goalLogRepository.save(glog);
        }

            return "alimentare si goal log adaugate cu success!";
        }

    public boolean deleteAlimentationById(Long id) {
        alimentationRepository.deleteById(id);
        if(alimentationRepository.findById(id).isEmpty()){
           return true;
        }
        else{
            throw new RuntimeException("Eroare la stergerea datelor de nutritie");
        }

    }
}
