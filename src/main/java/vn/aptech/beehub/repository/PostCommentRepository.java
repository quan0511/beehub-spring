package vn.aptech.beehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
	@Query("select pc from PostComment pc where pc.post.id = :id")
	List<PostComment> findCommentById(int id);
	void deleteByPostId(int id);
	List<PostComment> findByPost(Post post);
}
