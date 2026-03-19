package health.tracking.application.controller;

import health.tracking.application.dto.MessageDTO;
import health.tracking.application.entities.Message;
import health.tracking.application.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController { //este controllerul care gestioneaza partea de mesaje cu wbesocket


    private final MessageService messageService;

    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/send")
    public void processMessage(@Payload MessageDTO messageDTO){
        Message m=messageService.saveMessage(messageDTO);
        messagingTemplate.convertAndSendToUser(messageDTO.getReceiver(),
                "/queue/messages",
                m);
    }
}
