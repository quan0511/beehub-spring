package vn.aptech.beehub.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.Report;
import vn.aptech.beehub.models.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer>{
	@Query(value = "SELECT r.* FROM reports r WHERE r.target_post_id IN (SELECT p.id FROM posts p WHERE p.group_id = ?1)",nativeQuery = true)
	List<Report> findRepostPostInGroup(Long id_group);
	@Query(value = "SELECT r.* FROM reports r WHERE r.target_user_id = ?1",nativeQuery = true)
	List<Report> findAllByUserId(Long id);
	@Query(value = "SELECT r.* FROM reports r WHERE r.target_post_id = ?1",nativeQuery = true)
	List<Report> findAllByPostId(Long id);
	@Query(value = "SELECT r.* FROM reports r WHERE r.target_group_id = ?1",nativeQuery = true)
	List<Report> finaAllByGroupId(Long id);
	@Modifying(flushAutomatically = true)
	@Query(value = "DELETE FROM reports WHERE id = ?1",nativeQuery = true)
	void deleteReport(Integer id);
	@Modifying(flushAutomatically = true)
	@Query(value = "DELETE FROM reports WHERE target_post_id = ?1",nativeQuery = true)
	void deletePostReposts(Long id);
}
