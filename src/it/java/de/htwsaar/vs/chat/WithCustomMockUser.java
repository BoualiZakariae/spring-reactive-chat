package de.htwsaar.vs.chat;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.*;

/**
 * Different {@link WithMockUser} implementation using a custom {@link WithSecurityContextFactory}.
 *
 * @author Arthur Kelsch
 * @see WithMockUser
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {

    String id() default "42";

    String username() default "user";

    String password() default "password";

    String[] roles() default {"USER"};

    String[] authorities() default {};
}
