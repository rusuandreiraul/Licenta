package health.tracking.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AIService {

    private final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private RestTemplate restTemplate=new RestTemplate(); //restTemplate imi permite sa fac cereri din java ca si cum as fii in frontend


    public String askAI(String prompt) {
        Map<String, Object> request=Map.of(
                "model", "mistral",
                "prompt", prompt,
                "stream", false
        );

        try{
            //postForObject imi permite sa fac o cerere POST la url si trimite obiectul request
            //in final il transforma intrun Map (cu Map.class)
            Map<String, Object> response=restTemplate.postForObject(OLLAMA_URL,request, Map.class);
            return (String) response.get("response"); //returnez doar raspunsul la frontend
        }
        catch(Exception e){
            return "Eroare la comunicare cu AI: " + e.getMessage();
        }

    }
}
