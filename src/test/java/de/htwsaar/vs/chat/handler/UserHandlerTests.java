package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * JUnit tests for routes defined in {@link UserHandler}.
 *
 * @author kluhan
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserHandlerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser
    void getAllUsers() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("Test"));

        User user0 = new User();
        user0.setId("0");
        user0.setRoles(roles);

        User user1 = new User();
        user1.setId("1");
        user1.setRoles(roles);

        User user2 = new User();
        user2.setId("2");

        given(userService.findAll()).willReturn(Flux.just(user0, user1, user2));

        webTestClient
                .get().uri("/api/v1/users")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class)
                .hasSize(3);
    }

    @Test
    @WithMockUser
    void getAllUsersWithFilter() {

        User user0 = new User();
        user0.setId("0");
        user0.setUsername("testuser0");
        user0.setPassword("testpassword");
        user0.addRole(new SimpleGrantedAuthority("ROLE_TEST"));

        User user1 = new User();
        user1.setId("0");
        user1.setUsername("testuser1");
        user1.setPassword("testpassword");
        user1.addRole(new SimpleGrantedAuthority("ROLE_TEST"));

        User user2 = new User();
        user2.setId("2");
        user2.setUsername("testuser2");
        given(userService.findAll()).willReturn(Flux.just(user0, user1, user2));

        webTestClient
                .get().uri("/api/v1/users?username=testuser0")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class)
                .hasSize(1);
    }

    @Test
    @WithMockUser
    void getExistingUser() {

        User user2 = new User();
        user2.setId("2");

        given(userService.findById("2")).willReturn(Mono.just(user2));

        webTestClient
                .get().uri("/api/v1/users/{id}", user2.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(User.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(user2));
    }

    @Test
    @WithMockUser
    void getNonExistingUser() {

        given(userService.findById("4")).willReturn(Mono.empty());

        webTestClient
                .get().uri("/api/v1/users/{id}", "4")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void delete() {
        User user0 = new User();
        user0.setId("0");

        given(userService.deleteById("0")).willReturn(Mono.empty());

        webTestClient
                .delete().uri("/api/v1/users/{id}", user0.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @WithMockUser
    void changePassword() {

        User user0 = new User();
        user0.setId("0");
        user0.setUsername("testuser");
        user0.setPassword("testpassword");

        User user1 = new User();
        user1.setId("0");
        user1.setUsername("testuser");
        user1.setPassword("testpassword1");

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("oldPassword", "testpassword");
        payload.put("newPassword", "testpassword1");

        given(userService.findById("0")).willReturn(Mono.just(user0));
        given(userService.updatePassword(user0)).willReturn(Mono.just(user1));
        given(passwordEncoder.matches("testpassword", "testpassword")).willReturn(true);

        webTestClient
                .post().uri("/api/v1/users/{id}/change_password", user0.getId())
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void putRoleWithValidPayload() {

        User user0 = new User();
        user0.setId("0");
        user0.setUsername("testuser");
        user0.setPassword("testpassword");

        User user1 = new User();
        user1.setId("0");
        user1.setUsername("testuser");
        user1.setPassword("testpassword");
        user1.addRole(new SimpleGrantedAuthority("ROLE_TEST"));

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("role", "ROLE_TEST");

        given(userService.findById("0")).willReturn(Mono.just(user0));
        given(userService.updateRoles(user1)).willReturn(Mono.just(user0));

        webTestClient
                .put().uri("/api/v1/users/{id}/roles", user0.getId())
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void putRoleWithEmptyRole() {

        User user0 = new User();
        user0.setId("0");
        user0.setUsername("testuser");
        user0.setPassword("testpassword");


        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("role", "");

        given(userService.findById("0")).willReturn(Mono.just(user0));

        webTestClient
                .put().uri("/api/v1/users/{id}/roles", user0.getId())
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void putRoleWithInvalidRole() {

        User user0 = new User();
        user0.setId("0");
        user0.setUsername("testuser");
        user0.setPassword("testpassword");


        given(userService.findById("0")).willReturn(Mono.just(user0));

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("role", "_TEST");

        webTestClient
                .put().uri("/api/v1/users/{id}/roles", user0.getId())
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void deleteRole() {

        User user0 = new User();
        user0.setId("0");
        user0.setUsername("testuser");
        user0.setPassword("testpassword");
        user0.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));

        given(userService.findById("0")).willReturn(Mono.just(user0));

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("role", "ROLE_ADMIN");

        webTestClient
                .delete().uri("/api/v1/users/{id}/roles", user0.getId())
                .exchange()
                .expectStatus().isNoContent();
    }
}
