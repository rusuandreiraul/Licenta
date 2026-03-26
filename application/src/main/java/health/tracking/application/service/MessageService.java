package health.tracking.application.service;

import health.tracking.application.dto.MessageDTO;
import health.tracking.application.entities.Conversation;
import health.tracking.application.entities.Message;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.MessageMapper;
import health.tracking.application.repository.ConversationRepository;
import health.tracking.application.repository.MessageRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageMapper messageMapper;

    public Message saveMessage(MessageDTO messageDTO) {
        User sender=userRepository.findByEmailOrUsername(messageDTO.getSender(), messageDTO.getSender());
        User receiver=userRepository.findByEmailOrUsername(messageDTO.getReceiver(), messageDTO.getReceiver());

        Conversation conversation;
        Optional<Conversation> existingConv = conversationRepository.findConversationBetweenUsers(
                sender.getUsername(),
                receiver.getUsername()
        );

        if (existingConv.isPresent()) {
            conversation = existingConv.get();
        }
        else {
            conversation = new Conversation();
            conversation.setSender(sender);
            conversation.setReceiver(receiver);
            conversation = conversationRepository.save(conversation);
        }

        conversation.setLast_message(LocalDateTime.now());
        conversationRepository.save(conversation);
            Message m=new Message(); //si abia apoi daca am creat conversatia sau exsita adaugam mesajul in conversatie
            m.setContent(messageDTO.getContent());
            m.setSeen(false);
            m.setConversation(conversation);
            m.setTimestamp(LocalDateTime.now());
            m.setSender(sender);
            messageRepository.save(m);
            return m;
    }

    public List<MessageDTO> findAllMessages(String s, String r) {
        User userA = userRepository.findByEmailOrUsername(s, s);
        User userB = userRepository.findByEmailOrUsername(r, r);


        Optional<Conversation> c = conversationRepository.findConversationBetweenUsers(
                userA.getUsername(),
                userB.getUsername()
        );

        return c.map(conversation -> conversation.getMessages()
                        .stream()
                        .map(messageMapper::toDto)
                        .toList())
                .orElse(new ArrayList<>());
    }
}
