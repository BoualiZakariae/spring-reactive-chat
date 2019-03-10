package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.ChatRouter;
import de.htwsaar.vs.chat.service.ChatService;
import de.htwsaar.vs.chat.service.MessageService;
import de.htwsaar.vs.chat.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

/**
 * Integration tests for routes defined in {@link ChatRouter}.
 *
 * @author Klaus Luhan
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class ChatIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void getAllChats() {
        webTestClient
                .get().uri("/api/v1/chats")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Chat.class);
    }

    // TODO postChat()-test

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void deleteChatsByExistingId() {
        Chat chat = chatService.findAllChatsForCurrentUser().blockFirst();
        webTestClient
                .delete().uri("/api/v1/chats/{chatid}", chat.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertThat(chatService.findAllChatsForCurrentUser())
                .asList().contains(chat);
    }

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void deleteChatsByNonExistingId() {
        webTestClient
                .delete().uri("/api/v1/chats/non_existing_id")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }


    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void getMessageStream() {
        webTestClient
                .get().uri("/api/v1/chats/messages/stream")
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(TEXT_EVENT_STREAM)
                .expectBodyList(Message.class);
    }

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void getMembersByExistingChatId() {
        Chat chat = chatService.findAllChatsForCurrentUser().blockFirst();
        webTestClient
                .get().uri("/api/v1/chats/{chatid}/members", chat.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class);
    }

    @Test
    @WithMockUser
    void getMembersByNonExistingChatId() {
        webTestClient
                .get().uri("/api/v1/chats/non_existing_chatid/members")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().equals("[]");
    }

    // TODO postMember()-test

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void deleteMembersByExistingChatId() {
        Chat chat = chatService.findAllChatsForCurrentUser().blockFirst();
        User user = chat.getMembers().iterator().next();

        webTestClient
                .delete().uri("/api/v1/chats/{chatid}/members/{userid}", chat.getId(), user.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertThat(chatService.findAllMembers(chat.getId()))
                .asList().contains(user);
    }

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void deleteMembersByNonExistingIds() {
        webTestClient
                .delete().uri("/api/v1/chats/non_existing/members/non_existing")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void getAllMassagesByExistingChatId() {
        Chat chat = chatService.findAllChatsForCurrentUser().blockFirst();
        webTestClient
                .get().uri("/api/v1/chats/{chatid}/messages", chat.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Message.class);
    }

    @Test
    @WithMockUser
    void getAllMassagesByNonExistingChatId() {
        webTestClient
                .get().uri("/api/v1/chats/non_existing_id/messages")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().equals("[]");
    }

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void getAllMassagesPaginatedByExistingChatId() {
        Chat chat = chatService.findAllChatsForCurrentUser().blockFirst();
        webTestClient
                .get().uri("/api/v1/chats/{chatid}/messages/paginated", chat.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Message.class).hasSize(50);
    }

    //TODO: post-massage-test

    @Test
    @WithMockUser
    @Disabled("Problem with cast(UserPrincipal.class) and @PreAuthorize")
    void deleteMassagesByExistingMessageId() {
        Chat chat = chatService.findAllChatsForCurrentUser().blockFirst();
        Message message = messageService.findAllMessages(chat.getId()).blockFirst();

        webTestClient
                .delete().uri("/api/v1/chats/{chatid}/messages/{messageid}",chat.getId(), message.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertThat(messageService.findAllMessages(chat.getId()))
                .asList().contains(message);
    }

}
