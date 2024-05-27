package vn.aptech.beehub.services;

import java.util.List;
import java.util.Optional;

import vn.aptech.beehub.dto.PostDtoMe;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.User;

public interface PostService {
	List<Post> findAllPost();
	Post savePost(PostDtoMe dto);
	boolean deletePost(int id);
	Post updatePost(PostDtoMe dto);
	Optional<Post> findByIdPost(int id);
	List<User> findAllUser();
}
