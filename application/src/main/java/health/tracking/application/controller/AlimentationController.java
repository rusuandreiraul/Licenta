package health.tracking.application.controller;

import health.tracking.application.dto.AlimentationDTO;
import health.tracking.application.dto.SleepDTO;
import health.tracking.application.service.AlimentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class AlimentationController {

    @Autowired //Dependency Injection
    AlimentationService alimentationService;


    private final RestTemplate restTemplate=new RestTemplate();


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

    @GetMapping("/products/search")
    public ResponseEntity<?> searchProduct(@RequestParam String query) {


        System.out.println(">>> VALOAREA PRIMITĂ DIN FRONTEND ESTE: '" + query + "'");
        String myUserAgent = "WellSyncAppV2 - Testing Environment - Contact: andreiraul.rusu@gmail.com";

        // Validare rapidă ca să nu trimitem cereri inutile cu text gol
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.badRequest().body("Termenul de căutare trebuie să aibă cel puțin 2 caractere.");
        }

        try {
            // 1. Reconstruim URL-ul folosind noul API v2 și UriComponentsBuilder pentru encodare automată
            String url = UriComponentsBuilder.fromHttpUrl("https://ro.openfoodfacts.org/cgi/search.pl")
                    .queryParam("search_terms", query)         // Caută textul în toate câmpurile relevante (nume, brand, ingrediente)
                    .queryParam("search_simple", 1)           // Activează interfața simplă de căutare
                    .queryParam("action", "process")          // Pornește procesul de filtrare
                    .queryParam("json", 1)                    // Obligatoriu: cere răspunsul în format JSON, nu HTML
                    .queryParam("lc", "ro")                   // Setează limba preferată pentru denumiri (Română)
                    .queryParam("cc", "ro")                   // Filtrează produsele disponibile în România
                    .queryParam("fields", "code,product_name,brands,image_front_url,nutriments,nutriscore_grade") // Returnează doar ce ai nevoie
                    .queryParam("page_size", 50)              // Returnează până la 50 de variante de lapte
                    .queryParam("page", 1)
                    .build()
                    .toUriString();
            // 2. Setăm Headerele
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", myUserAgent);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 3. Executăm cererea HTTP
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // 4. Returnăm JSON-ul primit
            return ResponseEntity.ok(response.getBody());

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            // Prindem erorile HTTP venite SPECIFIC de la OpenFoodFacts (4xx, 5xx) ca să vedem exact răspunsul lor
            return ResponseEntity.status(e.getStatusCode())
                    .body("OpenFoodFacts a returnat eroarea: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare internă la comunicarea cu API-ul: " + e.getMessage());
        }
    }

    @PostMapping("dashboard-alimentation/{selectedDate}")//cerere POST de creare/salvare
    public ResponseEntity<?> addAlimentation(@RequestBody AlimentationDTO a, @PathVariable String selectedDate, Authentication authentication){
        //adnotatia @RequestBody este utilizata pentru a prelua informațiile din corpul cereri din frontend
        String username=authentication.getName();
        String response=alimentationService.saveAlimentation(a, username, selectedDate); //a va contine informațiile despre nutritie de adaugat in baza de date
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete-alimentation/{selectedId}")
    public ResponseEntity<?> deleteAlimentation(@PathVariable Long selectedId, Authentication authentication){
        boolean response=alimentationService.deleteAlimentationById(selectedId);
        if(response==true) {
            return ResponseEntity.ok("Aliment sters cu succes");
        }
        return ResponseEntity.noContent().build();
    }
}
