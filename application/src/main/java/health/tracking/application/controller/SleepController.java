package health.tracking.application.controller;

import health.tracking.application.dto.SleepDTO;
import health.tracking.application.service.SleepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class SleepController {

    @Autowired
    SleepService sleepService;

    @GetMapping("/sleep-week/{selectedDate}/{username}")
    public List<SleepDTO> getSleepWeek(@PathVariable String selectedDate, @PathVariable String username){
      return sleepService.findSleepWeekByDate(selectedDate, username);
    }

    @PostMapping("/dashboard-sleep/{username}/{selectedDate}")
    public ResponseEntity<?> addNewSleep(@RequestBody SleepDTO s, @PathVariable String username, @PathVariable String selectedDate){
        String response=sleepService.saveSleep(s, username, selectedDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard-sleep/{username}/{selectedDate}")
    public ResponseEntity<?> getSleepByDate(@PathVariable String username, @PathVariable String selectedDate){
        SleepDTO dto=sleepService.findSleepByDate(username, selectedDate);
        if(dto!=null){
            return ResponseEntity.ok(dto);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nu s-au găsit date de somn pentru această dată.");
        }
    }


}
