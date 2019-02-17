package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for {@link Chat}.
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 * @see ChatRepository
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Flux<Chat> findAllForUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserPrincipal.class)
                .flatMapMany(principal -> chatRepository.findAllByMembers(principal.getId()));
    }

    public Flux<User> findAllMembersForChat(String chatId) {
        return chatRepository
                .findById(chatId)
                .flatMapMany(chat -> Flux.fromIterable(chat.getMembers()));
    }

    @PostAuthorize("@webSecurity.addChatAuthority(authentication, #chat)")
    public Mono<Chat> save(Chat chat) {
        return chatRepository.save(chat);
    }

    public Mono<Chat> saveNewMember(String chatId, User member) {
        return chatRepository
                .findById(chatId)
                .doOnNext(chat -> chat.getMembers().add(member))
                .flatMap(chatRepository::save);
    }

    @PreAuthorize("hasAuthority('CHAT_' + #chatId + '_ADMIN') or #userId == principal.id")
    public Mono<Void> removeMember(String chatId, String userId) {
        return chatRepository
                .findById(chatId)
                .filter(chat -> chat.getMembers().removeIf(member -> member.getId().equals(userId)))
                .flatMap(chatRepository::save)
                .then();
    }
}
