package vn.aptech.beehub.services;

import java.util.List;

import vn.aptech.beehub.dto.PostReactionDto;
import vn.aptech.beehub.models.PostReaction;

public interface PostReactionService {
	List<PostReaction> findRecommentByComment(int id);
	PostReaction saveRecomment(PostReactionDto dto);
	int countReactionByComment(int commentId);
}
