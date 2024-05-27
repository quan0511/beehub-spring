package vn.aptech.beehub.services;

import java.util.List;

import vn.aptech.beehub.dto.LikeDto;
import vn.aptech.beehub.models.LikeUser;

public interface LikeService {
	LikeUser addLike(LikeDto dto);
	LikeUser updateLike(LikeDto dto);
	boolean removeLike(Long postId, Long userId);
	boolean checklike(Long postId, Long userId);
	String getEnumEmoByUserIdAndPostId(Long postId, Long userId);
	int countLikesByPost(Long postId);
	List<LikeUser> findLikeUserByPost(Long postId);
	List<LikeUser> findAllEmoByPost(Long postId);
	int countReactionByComment(int commentId);
	List<LikeUser> findEmoByPostEnum(Long postId,String emoji);
}
