package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.service.ChatService;
import de.htwsaar.vs.chat.service.MessageService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

/**
 * JUnit tests for {@link ChatHandler}.
 *
 * @author kluhan
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class ChatHandlerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ChatService chatService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser
    void getAllChats() {
        Chat chat0 = new Chat();
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();


        given(chatService.findAllChatsForCurrentUser()).willReturn(Flux.just(chat0, chat1, chat2));

        webTestClient
                .get().uri("/api/v1/chats")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Chat.class)
                .hasSize(3);
    }

    @Test
    @WithMockUser
    @Disabled
    void getPutChat() {
        Chat chat0 = new Chat();

        User user1 = new User();
        user1.setUsername("testusername");

        Map<String, String> payload = new LinkedHashMap<>();
        Set<User> members = new HashSet<>();
        members.add(user1);

        payload.put("name", "Chat0");
        payload.put("members", members.toString());

        chat0.setName("Chat0");
        chat0.setMembers(members);

        given(chatService.saveChat(chat0)).willReturn(Mono.just(chat0));

        webTestClient
                .put().uri("/api/v1/chats")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    @WithMockUser
    void deleteChat() {
        Chat chat0 = new Chat();
        chat0.setId("1");


        given(chatService.deleteChat("1")).willReturn(Mono.empty());

        webTestClient
                .delete().uri("/api/v1/chats/{id}", chat0.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }



    @Test
    @WithMockUser
    void getNewChat() {
        Chat chat0 = new Chat();
        chat0.setId("0");

        Chat chat1 = new Chat();
        chat1.setId("1");

        Chat chat2 = new Chat();
        chat2.setId("2");


        given(chatService.streamNewChats()).willReturn(Flux.just(chat0, chat1, chat2));

        webTestClient
                .get().uri("/api/v1/chats/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Chat.class).hasSize(3);
    }



    @Test
    @WithMockUser
    void getNewMassage() {
        Message message0 = new Message();
        message0.setId("0");

        Message message1 = new Message();
        message1.setId("1");

        Message message2 = new Message();
        message2.setId("2");

        given(chatService.streamNewMessages()).willReturn(Flux.just(message0, message1, message2));

        webTestClient
                .get().uri("/api/v1/chats//messages/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Message.class).hasSize(3);
    }

    @Test
    @WithMockUser
    void getAllMembers() {

        User user0 = new User();
        user0.setId("0");

        User user1 = new User();
        user1.setId("1");

        User user2 = new User();
        user2.setId("2");

        Chat chat0 = new Chat();
        chat0.setId("0");

        given(chatService.findAllMembers(chat0.getId())).willReturn(Flux.just(user0, user1, user2));

        webTestClient
                .get().uri("/api/v1/chats/{chatid}/members", chat0.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).hasSize(3);
    }

    @Test
    @WithMockUser
    void deleteMembers() {

        User user0 = new User();
        user0.setId("0");

        Chat chat0 = new Chat();
        chat0.setId("0");

        given(chatService.deleteMember(chat0.getId(), user0.getId())).willReturn(Mono.empty());

        webTestClient
                .delete().uri("/api/v1/chats/{chatid}/members/{userid}", chat0.getId(), user0.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @WithMockUser
    void getAllMessagesPaginated() {
        Message message0 = new Message();
        message0.setId("0");

        Message message1 = new Message();
        message1.setId("1");

        Message message2 = new Message();
        message2.setId("2");

        Chat chat0 = new Chat();
        chat0.setId("0");

        given(messageService.findAllMessagesPaginated(chat0.getId(), "1", "50")).willReturn(Flux.just(message0, message1, message2));

        webTestClient
                .get().uri("/api/v1/chats/{chatid}/messages/paginated", chat0.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Message.class).hasSize(3);
    }


    @Test
    @WithMockUser
    void deleteMessage() {

        Message massage0 = new Message();

        given(messageService.findById("0")).willReturn(Mono.just(massage0));
        given(messageService.deleteMessage(massage0)).willReturn(Mono.empty());

        webTestClient
                .delete().uri("/api/v1/chats/{chatid}/messages/{id}", "1", "0")
                .exchange()
                .expectStatus().isNoContent();
    }

    //TODO posts
}
