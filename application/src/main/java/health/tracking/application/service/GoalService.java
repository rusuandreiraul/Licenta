package health.tracking.application.service;

import health.tracking.application.dto.GoalDTO;
import health.tracking.application.entities.Goal;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.GoalMapper;
import health.tracking.application.repository.GoalRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    @Autowired
    GoalRepository goalRepository;

    @Autowired
    GoalMapper goalMapper;

    @Autowired
    UserRepository userRepository;

    public boolean saveGoals(List<GoalDTO> dtos, String username){
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            for(GoalDTO dto:dtos){
                Goal existing=goalRepository.findByUserAndType(u, dto.getType());
                Goal g;
                if(existing!=null){
                    g=existing;
                }
                else {
                    g = new Goal();
                    g.setType(dto.getType());
                    g.setUser(u);
                }
                g.setTargetValue(dto.getTargetValue());
                goalRepository.save(g);
            }
            return true;
        }
        return false;
    }

    public List<GoalDTO> findByUser(String username) {
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            List<Goal>g=goalRepository.findByUser(u);
            return g.stream().map(goalMapper::toDto).toList();
        }
        return null;
    }
}
