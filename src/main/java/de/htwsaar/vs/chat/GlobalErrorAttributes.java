package de.htwsaar.vs.chat;

import java.util.Map;

import com.mongodb.DuplicateKeyException;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;

import org.springframework.core.codec.DecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 *
 * @author Julian Quint
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes{

    public GlobalErrorAttributes() {
        super(false);
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(request, includeStackTrace);
        Throwable error = getError(request);
        if(error.getClass() == DecodingException.class || error.getClass() == ConstraintViolationException.class) {
            map.put("status", BAD_REQUEST.value());
            map.put("error", BAD_REQUEST.getReasonPhrase());
        } if(error.getClass() == DuplicateKeyException.class) {
            map.put("status", CONFLICT.value());
            map.put("error", CONFLICT.getReasonPhrase());
        }
        return map;
    }

}


