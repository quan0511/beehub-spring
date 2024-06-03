package vn.aptech.beehub.services;

import java.util.List;
import java.util.Optional;

import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.PostMeDto;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.RelationshipUsers;
import vn.aptech.beehub.models.SharePost;
import vn.aptech.beehub.models.User;

public interface PostService {
	List<Post> findAllPost();
	Post savePost(PostMeDto dto);
	boolean deletePost(Long id);
	Post updatePost(PostMeDto dto);
	Optional<Post> findByIdPost(Long id);
	List<User> findAllUser();
	Post sharePost(PostMeDto dto);
}
