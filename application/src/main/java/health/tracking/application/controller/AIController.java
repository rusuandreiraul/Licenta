package health.tracking.application.controller;


import health.tracking.application.service.AIService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:3000/")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService){
        this.aiService=aiService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> payload){ // aici trimit {message: "mesajul userului"}
        String userMessage=payload.get("message");
        String aiResponse=aiService.askAI(userMessage);
        return Map.of("text", aiResponse);

    }

}
