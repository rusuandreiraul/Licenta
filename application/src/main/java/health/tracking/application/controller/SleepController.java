package health.tracking.application.controller;

import health.tracking.application.dto.SleepDTO;
import health.tracking.application.service.SleepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class SleepController {

    @Autowired
    SleepService sleepService;

    @GetMapping("/sleep-week/{selectedDate}")
    public List<SleepDTO> getSleepWeek(@PathVariable String selectedDate, Authentication authentication){
        String username=authentication.getName();
      return sleepService.findSleepWeekByDate(selectedDate, username);
    }

    @PostMapping("/dashboard-sleep/{selectedDate}")
    public ResponseEntity<?> addNewSleep(@RequestBody SleepDTO s, Authentication authentication, @PathVariable String selectedDate){
        String username=authentication.getName();
        String response=sleepService.saveSleep(s, username, selectedDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard-sleep/{selectedDate}")
    public ResponseEntity<?> getSleepByDate(Authentication authentication, @PathVariable String selectedDate){
        String username=authentication.getName();
        System.out.println(username);
        SleepDTO dto=sleepService.findSleepByDate(username, selectedDate);
        if(dto!=null){
            return ResponseEntity.ok(dto);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nu s-au găsit date de somn pentru această dată.");
        }
    }


}
