package vn.aptech.beehub.services;

import java.util.List;

import vn.aptech.beehub.dto.PostCommentDto;
import vn.aptech.beehub.models.PostComment;

public interface PostCommentService {
	List<PostComment> findCommentById(int id);
	PostComment saveComment(PostCommentDto dto);
}
