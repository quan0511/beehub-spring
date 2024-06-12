package vn.aptech.beehub.services;

import java.util.List;
import java.util.Optional;

import vn.aptech.beehub.dto.PostDto;

public interface IPostService {
	public List<PostDto> findByUserId(Long id);
	public List<PostDto> newestPostsForUser(Long id,int page,int limit);
	public List<PostDto> getSearchPosts(String search,Long id);
	public List<PostDto> newestPostInGroup(Long id_group, Long id_user, int limit,int page);
	public List<PostDto> getPostsForUser(Long id,int page, int limit);
	public List<PostDto>  getAllPostForUser(Long id);
	public Optional<PostDto> getPost(Long id_user, Long id_post);
	public List<PostDto> findUserPosts(Long id_user,String username,int page,int limit);
}
