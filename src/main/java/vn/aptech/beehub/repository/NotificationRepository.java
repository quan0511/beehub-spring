package vn.aptech.beehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.aptech.beehub.models.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
	@Query("select n from Notification n where n.user.id = :userid")
	List<Notification> findNoteByUser(Long userid);
	@Query("select n from Notification n where n.user.id = :userid and n.seen = false ")
	List<Notification> findNoteSeenByUser(Long userid);
}
