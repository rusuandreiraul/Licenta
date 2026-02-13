package health.tracking.application.controller;

import health.tracking.application.dto.GoalDTO;
import health.tracking.application.entities.Goal;
import health.tracking.application.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class GoalController {

    @Autowired
    GoalService goalService;

    @GetMapping("/goals/{username}")
    public ResponseEntity<?> getGoals(@PathVariable String username){
        List<GoalDTO> goals=goalService.findByUser(username);
        if(goals!=null)
        return ResponseEntity.ok(goals);
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/set-goals/{username}")
    public String addGoals(@RequestBody List<GoalDTO> dtos, @PathVariable String username){
        boolean response=goalService.saveGoals(dtos, username);
        if(response==true){
            return "Adaugare cu success!";
        }
        else{
            return "Obiectivele nu au fost setate";
        }
    }

}
