package vn.aptech.beehub.services;

import java.util.List;
import java.util.Optional;

import vn.aptech.beehub.dto.PostReactionDto;
import vn.aptech.beehub.models.PostReaction;

public interface PostReactionService {
	List<PostReaction> findRecommentByComment(int id);
	PostReaction saveRecomment(PostReactionDto dto);
	int countReactionByComment(int commentId);
	PostReaction editRecomment(PostReactionDto dto);
	boolean deletePostReaction(int id);
	Optional<PostReaction> findReactionById(int id);
	int CountReactionByPost(Long postid);
}
