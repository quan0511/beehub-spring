package vn.aptech.beehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.aptech.beehub.models.Message;


public interface MessageRepository extends JpaRepository<Message, Integer> {
}
