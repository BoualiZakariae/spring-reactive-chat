package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link ChatService}.
 *
 * @author kluhan
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTests {


    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ReactiveMongoOperations mongoOperations;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(chatRepository, mongoOperations);
    }

    @Test
    @Disabled
    void findAll() {
        Chat chat0 = new Chat();
        chat0.setId("0");
        chat0.setName("testchat0");

        Chat chat1 = new Chat();
        chat1.setId("1");
        chat1.setName("testchat1");

        given(chatRepository.findAllByMembers(any())).willReturn(Flux.just(chat0, chat1));

        StepVerifier.create(chatService.findAllChatsForCurrentUser())
                .expectNextMatches(u -> u.getName().equals("testchat0"))
                .expectNextMatches(u -> u.getName().equals("testchat1"))
                .expectComplete();
    }

    @Test
    void delete() {

        given(chatRepository.deleteById("0")).willReturn(Mono.empty());

        StepVerifier.create(chatService.deleteChat("0"))
                .expectComplete();
    }

    @Test
    void findAllMembers() {

        User user0 = new User();
        user0.setId("0");

        User user1 = new User();
        user1.setId("1");

        User user2 = new User();
        user1.setId("2");

        Set<User> set = new HashSet<User>();
        set.add(user0);
        set.add(user1);
        set.add(user2);

        Chat chat0 = new Chat();
        chat0.setId("0");
        chat0.setName("testchat0");

        chat0.setMembers(set);

        given(chatRepository.findById(chat0.getId())).willReturn(Mono.just(chat0));

        StepVerifier.create(chatService.findAllMembers(chat0.getId()))
                .expectNextMatches(u -> u.getId().equals("0"))
                .expectNextMatches(u -> u.getId().equals("1"))
                .expectComplete();
    }

    @Test
    void saveMembers() {

        User user0 = new User();
        user0.setId("0");

        User user1 = new User();
        user1.setId("1");

        User user2 = new User();
        user1.setId("2");

        Set<User> set = new HashSet<User>();
        set.add(user0);
        set.add(user1);

        Chat chat0 = new Chat();
        chat0.setId("0");
        chat0.setName("testchat0");
        chat0.setMembers(set);


        given(chatRepository.findById(chat0.getId())).willReturn(Mono.just(chat0));

        StepVerifier.create(chatService.saveMember(chat0.getId(), user2))
                .expectNextMatches(u -> u.getId().equals("2"))
                .expectComplete();
    }


    @Test
    void deleteMembers() {

        User user0 = new User();
        user0.setId("0");

        User user1 = new User();
        user1.setId("1");

        User user2 = new User();
        user1.setId("2");

        Set<User> set = new HashSet<User>();
        set.add(user0);
        set.add(user1);
        set.add(user2);

        Chat chat0 = new Chat();
        chat0.setId("0");
        chat0.setName("testchat0");
        chat0.setMembers(set);


        given(chatRepository.findById(chat0.getId())).willReturn(Mono.just(chat0));

        StepVerifier.create(chatService.deleteMember(chat0.getId(), user2.getId()))
                .expectComplete();
    }

    //TODO: streamNewMessages-Test



}