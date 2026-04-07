package health.tracking.application.controller;

import health.tracking.application.dto.MessageDTO;
import health.tracking.application.entities.Message;
import health.tracking.application.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
public class MessageController { //este controllerul care gestioneaza partea de mesaje cu wbesocket


    private final MessageService messageService;

    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/send")
    public void processMessage(@Payload MessageDTO messageDTO, Authentication authentication){
        String sender=authentication.getName();

        messageDTO.setSender(sender);
        Message m=messageService.saveMessage(messageDTO);
        messagingTemplate.convertAndSendToUser(messageDTO.getReceiver(),
                "/queue/messages",
                m);//acesta este payloadul care va trimis catre frontend
    }

    @GetMapping("/chat-history/{receiver}")
    public List<MessageDTO> getMessages(@PathVariable String receiver, Authentication authentication){
        String sender=authentication.getName();
        System.out.println("DEBUG: SENDER E: " + sender);
        return messageService.findAllMessages(sender, receiver);
    }
}
