package vn.aptech.beehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.aptech.beehub.models.GroupMember;
import vn.aptech.beehub.models.MessageRecipient;

import java.util.List;
import java.util.Optional;

public interface MessageRecipientRepository extends JpaRepository<MessageRecipient, Integer> {

    @Query("""
            SELECT mr
            FROM Message m
            JOIN MessageRecipient mr ON m.id = mr.message.id
            WHERE (m.creator.id = :user1 AND mr.recipient.id = :user2)
               OR (m.creator.id = :user2 AND mr.recipient.id = :user1)
            ORDER BY m.createAt""")
    List<MessageRecipient> findAllByUserVsUser(Long user1, Long user2);

    @Query(value = """
            SELECT * FROM (SELECT mr.*
            FROM group_members gm
            JOIN message_recipient mr ON gm.id = mr.recipient_group_id
            JOIN message m ON m.id = mr.message_id
            WHERE gm.group_id = :id
            ORDER BY m.create_at)x GROUP BY x.message_id""", nativeQuery = true)
    List<MessageRecipient> findAllByGroupIdOrderByCreatedAt(Long id);
}

