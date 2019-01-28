package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.auth.Role;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Collections;

/**
 * Service layer for {@link User}.
 *
 * @author Arthur Kelsch
 * @see UserRepository
 */
@Service
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<User> save(@Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singletonList(Role.USER));

        return userRepository.save(user);
    }

    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public Mono<Void> deleteById(String id) {
        return userRepository.deleteById(id);
    }
}