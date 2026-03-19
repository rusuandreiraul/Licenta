package health.tracking.application.service;

import health.tracking.application.dto.MessageDTO;
import health.tracking.application.entities.Conversation;
import health.tracking.application.entities.Message;
import health.tracking.application.entities.User;
import health.tracking.application.repository.ConversationRepository;
import health.tracking.application.repository.MessageRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;

    public Message saveMessage(MessageDTO messageDTO) {
        Conversation conv=conversationRepository.findById(messageDTO.getId_conversation())
        if(conv!=null){
            User sender=userRepository.findByEmailOrUsername(messageDTO.getSender(), messageDTO.getSender()).orElseThrow(() -> new RuntimeException("Sender-ul nu a fost găsit!"));
            Message m=new Message();
            m.setContent(messageDTO.getContent());
            m.setSeen(false);
            m.setConversation(conv);
            m.setTimestamp(LocalDateTime.now());
            m.setSender(sender);
        }

    }
}
