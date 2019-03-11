package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import de.htwsaar.vs.chat.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates a {@link SecurityContext} containing a custom {@link UserDetails}
 * implementation, namely {@link UserPrincipal}.
 *
 * @author Arthur Kelsch
 */
public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser mockUser) {
        List<GrantedAuthority> roles = Arrays.stream(mockUser.roles())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        List<GrantedAuthority> authorities = Arrays.stream(mockUser.authorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = new User();
        user.setId(mockUser.id());
        user.setUsername(mockUser.username());
        user.setPassword(mockUser.password());
        user.setRoles(roles);
        user.setAuthorities(authorities);

        UserDetails principal = new UserPrincipal(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
