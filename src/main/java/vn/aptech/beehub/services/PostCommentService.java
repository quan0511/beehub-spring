package vn.aptech.beehub.services;

import java.util.List;
import java.util.Optional;

import vn.aptech.beehub.dto.PostCommentDto;
import vn.aptech.beehub.models.PostComment;

public interface PostCommentService {
	List<PostComment> findCommentById(int id);
	PostComment saveComment(PostCommentDto dto);
	PostComment editComment(PostCommentDto dto);
	boolean deleteComment(int id);
	Optional<PostComment> findCommenyById(int id);
}
