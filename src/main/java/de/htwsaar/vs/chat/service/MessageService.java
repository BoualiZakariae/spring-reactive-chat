package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.repository.ChatRepository;
import de.htwsaar.vs.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for {@link Message}.
 *
 * @author Niklas Reinhard
 * @see ChatRepository
 */
@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    public Flux<Message> findAllMessagesForChat(String chatId){
        return messageRepository.findAllByChatId(chatId);
    }

    public Mono<Message> addMessageToChat(Message message){
        return messageRepository.save(message);
    }
}
