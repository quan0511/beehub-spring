package vn.aptech.beehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.aptech.beehub.models.LikeUser;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.User;

public interface LikeRepository extends JpaRepository<LikeUser, Integer> {
	LikeUser findByPostAndUser(Post post, User user);
	List<LikeUser> findByPost(Post post);
	@Query("select lu from LikeUser lu where lu.post.id = :postId")
	List<LikeUser> findEmoByPost(@Param("postId") int postId);
	@Query("select lu from LikeUser lu where lu.post.id = :postId and lu.enumEmo = :emoji")
    List<LikeUser> findEmoByPostEnum(@Param("postId") int postId, @Param("emoji") String emoji);
	void deleteByPostId(int id);
}
