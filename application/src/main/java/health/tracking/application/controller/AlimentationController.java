package health.tracking.application.controller;

import health.tracking.application.dto.AlimentationDTO;
import health.tracking.application.dto.SleepDTO;
import health.tracking.application.service.AlimentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class AlimentationController {

    @Autowired //Dependency Injection
    AlimentationService alimentationService;

    @GetMapping("/alimentation-data/{date}")//Citire
    public ResponseEntity<?> getAlimentationByDate(@PathVariable String date, Authentication authentication){
        //data este preluata din URL de acea se foloseste @PathVariable
        String username=authentication.getName();//authentication este injectat automat de sprin security folosit pentru a extrage username folosind token-ul din header
        List<AlimentationDTO> dto=alimentationService.findAlimentationDate(username, date); //informațiile sunt transmise catre logica de business
        if(dto!=null){
            return ResponseEntity.ok(dto); //Daca lista conține alimente se vor transmite către frontend
        }
        else{
            return ResponseEntity.status(200).body(Collections.emptyList());//returneaza o lista goala fară date pentru ziua respectiva
        }
    }

    @PostMapping("dashboard-alimentation/{selectedDate}")//cerere POST de creare/salvare
    public ResponseEntity<?> addAlimentation(@RequestBody AlimentationDTO a, @PathVariable String selectedDate, Authentication authentication){
        //adnotatia @RequestBody este utilizata pentru a prelua informațiile din corpul cereri din frontend
        String username=authentication.getName();
        String response=alimentationService.saveAlimentation(a, username, selectedDate); //a va contine informațiile despre nutritie de adaugat in baza de date
        return ResponseEntity.ok(response);
    }
}
