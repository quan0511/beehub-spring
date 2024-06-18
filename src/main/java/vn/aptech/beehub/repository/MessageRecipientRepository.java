package vn.aptech.beehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.aptech.beehub.models.MessageRecipient;

import java.util.List;

public interface MessageRecipientRepository extends JpaRepository<MessageRecipient, Integer> {

    @Query("""
            SELECT mr
            FROM Message m
            JOIN MessageRecipient mr ON m.id = mr.message.id
            WHERE (m.creator.id = :user1 AND mr.recipient.id = :user2)
               OR (m.creator.id = :user2 AND mr.recipient.id = :user1)
            ORDER BY m.createAt""")
    List<MessageRecipient> findMessagesForUser(Long user1, Long user2);
}

