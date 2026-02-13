package health.tracking.application.controller;

import health.tracking.application.dto.DashboardDailyDTO;
import health.tracking.application.dto.DashboardWeekDTO;
import health.tracking.application.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/dashboard-daily/{username}/{selectedDate}")
    public ResponseEntity<?> getDailyDashboard(@PathVariable String username, @PathVariable String selectedDate){
         DashboardDailyDTO dto=dashboardService.getDailyData(username, selectedDate);
         if(dto!=null){
            return ResponseEntity.ok(dto);
         }
         else{
             return ResponseEntity.status(404).body("Informatiile nu au fost preluate cu success");
         }
    }

    @GetMapping("/dashboard-week/{username}/{selectedDate}")
    public ResponseEntity<?> getWeekDashboard(@PathVariable String username, @PathVariable String selectedDate) {
        DashboardWeekDTO dto = dashboardService.getWeekData(username, selectedDate);
        if(dto!=null){
            return ResponseEntity.ok(dto);
        }
        else{
            return ResponseEntity.badRequest().body("Eroare la extragerea informatiilor");
        }
    }

}
