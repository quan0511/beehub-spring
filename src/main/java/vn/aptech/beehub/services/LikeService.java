package vn.aptech.beehub.services;

import java.util.List;

import vn.aptech.beehub.dto.LikeDto;
import vn.aptech.beehub.models.LikeUser;

public interface LikeService {
	LikeUser addLike(LikeDto dto);
	LikeUser updateLike(LikeDto dto);
	boolean removeLike(int postId, int userId);
	boolean checklike(int postId, int userId);
	String getEnumEmoByUserIdAndPostId(int postId, int userId);
	int countLikesByPost(int postId);
	List<LikeUser> findLikeUserByPost(int postId);
	List<LikeUser> findAllEmoByPost(int postId);
	int countReactionByComment(int commentId);
	List<LikeUser> findEmoByPostEnum(int postId,String emoji);
}
