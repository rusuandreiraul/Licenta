package health.tracking.application.controller;

import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.service.GoalLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LeaderboardController {

    @Autowired
    GoalLogService goalLogService;

    @GetMapping("/leaderboard")
    public List<UserResponseDTO> getLeaderBoard(Authentication authentication){
        String username=authentication.getName();
        List<UserResponseDTO> top3=goalLogService.getLeaders(username);
        if(top3.size()==3){
            return top3;
        }
        else{
            return null;
        }
    }
}
