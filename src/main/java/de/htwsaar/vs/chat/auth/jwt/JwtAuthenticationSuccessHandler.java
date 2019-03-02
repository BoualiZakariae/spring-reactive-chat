package de.htwsaar.vs.chat.auth.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static de.htwsaar.vs.chat.util.JwtUtils.JWT_PREFIX;
import static de.htwsaar.vs.chat.util.JwtUtils.createToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Adds an Authorization HTTP header with a token to the response.
 *
 * @author Arthur Kelsch
 */
public class JwtAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        exchange.getResponse().getHeaders()
                .add(AUTHORIZATION, JWT_PREFIX + createToken(authentication));

        return webFilterExchange.getChain().filter(exchange);
    }
}
