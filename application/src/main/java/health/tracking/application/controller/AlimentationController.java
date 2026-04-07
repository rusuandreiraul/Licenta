package health.tracking.application.controller;

import health.tracking.application.dto.AlimentationDTO;
import health.tracking.application.dto.SleepDTO;
import health.tracking.application.service.AlimentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class AlimentationController {

    @Autowired
    AlimentationService alimentationService;

    @GetMapping("/alimentation-data/{date}")
    public ResponseEntity<?> getAlimentationByDate(@PathVariable String date, Authentication authentication){
        String username=authentication.getName();
        List<AlimentationDTO> dto=alimentationService.findAlimentationDate(username, date);
        if(dto!=null){
            return ResponseEntity.ok(dto);
        }
        else{
            return ResponseEntity.badRequest().body("Ceva nu a functionat");
        }
    }

    @PostMapping("dashboard-alimentation/{selectedDate}")
    public ResponseEntity<?> addAlimentation(@RequestBody AlimentationDTO a, @PathVariable String selectedDate, Authentication authentication){
        String username=authentication.getName();
        String response=alimentationService.saveAlimentation(a, username, selectedDate);
        return ResponseEntity.ok(response);
    }
}
