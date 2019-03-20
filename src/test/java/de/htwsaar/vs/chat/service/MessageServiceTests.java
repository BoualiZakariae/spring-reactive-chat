package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.MessageRepository;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link MessageService}.
 *
 * @author kluhan
 */
@ExtendWith(MockitoExtension.class)
class MessageServiceTests {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository);
    }

    @Test
    void findAllMessages() {
        Message message0 = new Message();
        Message message1 = new Message();

        given(messageRepository.findAllByChatId("0")).willReturn(Flux.just(message0, message1));

        StepVerifier.create(messageService.findAllMessages("0"))
                .expectNext(message0)
                .expectNext(message1)
                .verifyComplete();
    }

    @Test
    void findAllMessagesPaginated() {
        Message message0 = new Message();
        Message message1 = new Message();
        Message message2 = new Message();

        given(messageRepository.findAllByChatId(any(), any())).willReturn(Flux.just(message0, message1, message2));

        StepVerifier.create(messageService.findAllMessagesPaginated("0", "0", "3"))
                .expectNextMatches(u -> u.equals(message0))
                .expectNextMatches(u -> u.equals(message1))
                .expectNextMatches(u -> u.equals(message2))
                .verifyComplete();
    }

    @Test
    @Disabled
    void save() {
        Message message0 = new Message();
        message0.setContent("Test");

        given(messageRepository.save(any())).willReturn(Mono.just(message0));

        StepVerifier.create(messageService.saveMessage(message0, "0"))
                .expectNextMatches(m -> m.getContent().equals("Test"))
                .verifyComplete();
    }


    @Test
    void delete() {
        Message message0 = new Message();
        message0.setContent("Test");
        message0.setId("0");

        given(messageRepository.delete(message0)).willReturn(Mono.empty());

        StepVerifier.create(messageService.deleteMessage(message0))
                .verifyComplete();
    }

}
