package vn.aptech.beehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.PostReaction;

public interface PostReactionRepository extends JpaRepository<PostReaction, Integer> {
	@Query("select pr from PostReaction pr where pr.postComment.id = :id")
	List<PostReaction> findReactionById(int id);
	List<PostReaction> findByPostComment(PostComment postComment);
	void deleteByPostId(int id);
	List<PostReaction> findByPost(Post post);
}
