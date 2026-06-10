package health.tracking.application.service;

import health.tracking.application.entities.Activity;
import health.tracking.application.entities.Alimentation;
import health.tracking.application.entities.Sleep;
import health.tracking.application.entities.User;
import health.tracking.application.repository.ActivityRepository;
import health.tracking.application.repository.AlimentationRepository;
import health.tracking.application.repository.SleepRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private SleepRepository sleepRepository;
    @Autowired
    private AlimentationRepository alimentationRepository;
    @Autowired
    private UserRepository userRepository;


   /* public String generateOverview(String username) {
        //1. se preaia prima zi
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

        User user=userRepository.findByEmailOrUsername(username,username);

        //2. se genereaza raport pe ultimele 3 zile (pentru activitate, somn si nutritie)
        List<Activity> activities = activityRepository.findAllByUserAndActivityDateAfter(user, threeDaysAgo);
        List<Sleep> sleepRecords = sleepRepository.findAllByUserAndDateSleepAfter(user, threeDaysAgo);
        List<Alimentation> nutrition = alimentationRepository.findAllByUserAndMealDateAfter(user, threeDaysAgo);

        //3. se creaza template-ul pe care modelul AI il va urma
        String template = """
        Ești un asistent virtual de sănătate pentru utilizatorul {username}.
        Datele din ultimele 3 zile sunt: {stats}.
        
        Cerințe:
        1. Analizează progresul utilizatorului.
        2. Oferă un rezumat scurt și motivațional.
        3. Propune 3 sfaturi concrete bazate strict pe aceste date.
        REGULĂ STRICTĂ: Răspunde DOAR în text simplu. 
        NU folosi deloc steluțe (**), diezi (###) sau alte marcaje Markdown. 
        Mapează listele doar cu cifre (1, 2, 3) și separă ideile prin rânduri noi.
        """;

        //4. se introduc variabilele dinamice in prompt
        String stats=String.format("Activitati: %s; Somn: %s; Alimentatie: %s", activities.toString(), sleepRecords.toString(), nutrition.toString());
        PromptTemplate promptTemplate=new PromptTemplate(template);
        Prompt prompt=promptTemplate.create(Map.of(
                "username",username,
                "stats", stats
                ));

        //5. se genereaza apelul cu promptul
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }*/

    public String processUserMessage(String message){
        // 1. se defineste regula de comportament si formatare a sistemului
        SystemMessage systemMessage = new SystemMessage("""
        Ești un asistent de sănătate. 
        REGULĂ STRICTĂ: Răspunde DOAR în text simplu. 
        NU folosi deloc steluțe (**), diezi (###) sau alte marcaje Markdown. 
        Mapează listele doar cu cifre (1, 2, 3) și separă ideile prin rânduri noi.
    """);

        // 2. Mesajul primit efectiv de la utilizator din frontend
        UserMessage userRequest = new UserMessage(message);

        // 3. ambele mesaje (de sistem si din partea utilizatorului) se pun in prompt si se face apelul
        Prompt prompt = new Prompt(List.of(systemMessage, userRequest));
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }


}
