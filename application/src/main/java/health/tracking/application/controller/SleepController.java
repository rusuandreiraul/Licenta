package health.tracking.application.controller;

import health.tracking.application.dto.SleepDTO;
import health.tracking.application.service.SleepService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

@RestController //Controller RESTful
@CrossOrigin(origins = "http://localhost:3000/")
public class SleepController {

    @Autowired // Dependency Injection
    SleepService sleepService;

    @GetMapping("/sleep-week/{selectedDate}") //Cerere GET pentru afisare
    public ResponseEntity<?> getSleepWeek(@PathVariable String selectedDate, Authentication authentication){
        //@PathVariable asociaza parametrul din URL cu argumentul din metoda Java
        String username=authentication.getName();
        List<SleepDTO> listSleep=sleepService.findSleepWeekByDate(selectedDate, username);
        if(listSleep.isEmpty()){
            return ResponseEntity.status(200).body(Collections.emptyList());
        }
        return ResponseEntity.ok(listSleep);
    }

    @PostMapping("/dashboard-sleep/{selectedDate}") //Cerere POST pentru creare
    public ResponseEntity<?> addNewSleep(@Valid @RequestBody SleepDTO s, Authentication authentication, @PathVariable String selectedDate){
        //@RequestBody utilizat pentru a extrage informatiile din corpul cereri
        //Authentication, gestionat de Spring Security, folosit pentru a prelua username din token-ul JWT
        //@Valid utilizat pentru validarea datelor care vin din DTO
        String username=authentication.getName();
        String response=sleepService.saveSleep(s, username, selectedDate); //informatiile sunt transmise catre logica de business (partea de service)
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-sleep/{selectedId}")
    public ResponseEntity<?> deleteSleep(@PathVariable Long selectedId, Authentication authentication){
        String username=authentication.getName();
        String response=sleepService.deleteSleep(selectedId);
    }

    @GetMapping("/dashboard-sleep/{selectedDate}")
    public ResponseEntity<?> getSleepByDate(Authentication authentication, @PathVariable String selectedDate){
        String username=authentication.getName();
        SleepDTO dto=sleepService.findSleepByDate(username, selectedDate);
        if(dto!=null){
            return ResponseEntity.ok(dto);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nu s-au găsit date de somn pentru această dată.");
        }
    }

}
