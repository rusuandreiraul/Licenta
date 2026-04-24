package health.tracking.application.controller;

import health.tracking.application.dto.UserResponseDTO;
import health.tracking.application.service.GoalLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class GoalLogController {

    @Autowired
    GoalLogService goalLogService;

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserResponseDTO>> getLeaderBoard(){
        List<UserResponseDTO> top3=goalLogService.getLeaders();
        return ResponseEntity.ok(top3!=null?top3: Collections.emptyList());
    }
}
