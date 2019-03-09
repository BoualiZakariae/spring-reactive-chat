package de.htwsaar.vs.chat.auth;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.MessageRepository;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Class that contains Web Security Expressions used by Spring Security
 * (e.g. in annotations like {@link PreAuthorize} and {@link PostAuthorize}).
 *
 * @author Arthur Kelsch
 * @author Julian Quint
 * @author Mahan Karimi
 */
@Component
public class WebSecurity {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;


    @Autowired
    public WebSecurity(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public boolean hasChatAuthority(Authentication authentication, String chatId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        return user.getAuthorities().contains(new ChatAuthority(chatId));
    }

    public boolean addChatAuthority(Authentication authentication, Chat chat) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        user.addAuthority(new ChatAuthority(chat.getId()));
        userRepository.save(user).subscribe();

        return true;
    }

    public boolean removeChatAuthority(Authentication authentication, String chatId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        if (user.removeAuthority(new ChatAuthority(chatId))) {
            userRepository.save(user).subscribe();
        }

        return true;
    }

    public boolean deleteMessageAuthority(Authentication authentication, String messageId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();
        String senderId = messageRepository.findById(messageId).block().getSender().getId();

        if (user.getId().equals(senderId)) {
            messageRepository.deleteById(messageId).subscribe();
        }
        return true;
    }

}
