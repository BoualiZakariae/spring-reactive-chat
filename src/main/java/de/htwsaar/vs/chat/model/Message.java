package de.htwsaar.vs.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Message object model (MongoDB Document).
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 */
@Data
@NoArgsConstructor
@Document
public class Message {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    @DBRef
    private Chat chat;

    @DBRef
    private User sender;

    private String content;
}
