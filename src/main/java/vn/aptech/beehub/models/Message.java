package vn.aptech.beehub.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"messageRecipients", "childrenMessage"})
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String subject;

    @Lob
    private String messageBody;

    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;

    @OneToMany(mappedBy = "parentMessage", fetch = FetchType.LAZY)
    private List<Message> childrenMessage;

    @OneToMany(mappedBy = "message")
    private List<MessageRecipient> messageRecipients;

}
