package health.tracking.application.controller;

import health.tracking.application.dto.ActivityChartSeriesDTO;
import health.tracking.application.dto.ActivityDTO;
import health.tracking.application.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/dashboard-activity/{dateRequest}")
    public ResponseEntity<?> getActivityByDate(@PathVariable String dateRequest, Authentication authentication){
        String username=authentication.getName(); //luam numele din token
        List<ActivityDTO> activities=activityService.getActivityByDate(username, dateRequest);
        if(activities!=null)
        return ResponseEntity.ok(activities);
        else
            return ResponseEntity.badRequest().body("Eroare la cererea activitatilor");
    }

    @PostMapping("/dashboard-activity/{date}")
    public ResponseEntity<?> addNewActivity(@RequestBody ActivityDTO activityDTO, @PathVariable String date, Authentication authentication){
        String username=authentication.getName(); //luam numele din token

        String response= activityService.addActivity(activityDTO, username, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart-series/{date}")
    public List<ActivityChartSeriesDTO> getSeriesChart(@PathVariable String date, Authentication authentication){
        String username=authentication.getName();
        return activityService.getSeriesByDate(username, date);
    }

}
