package health.tracking.application.controller;


import health.tracking.application.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController

@CrossOrigin(origins = "http://localhost:3000/")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService){
        this.aiService=aiService;
    }

    /*@GetMapping("/ai/daily-overview")
    public ResponseEntity<String> getOverview(Authentication authentication){
        String username=authentication.getName();
        return ResponseEntity.ok(aiService.generateOverview(username));
    }*/

    @PostMapping("/ai/chat")
    public ResponseEntity<String> chat(@RequestBody String message, Authentication authentication){
        String username=authentication.getName();
        return ResponseEntity.ok(aiService.processUserMessage(message, username));
    }

}
