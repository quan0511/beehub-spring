package vn.aptech.beehub.services.impl;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.aptech.beehub.dto.GalleryDto;
import vn.aptech.beehub.dto.GroupMediaDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.models.ESettingType;
import vn.aptech.beehub.models.Group;
import vn.aptech.beehub.models.GroupMember;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.repository.GroupMemberRepository;
import vn.aptech.beehub.repository.GroupRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.services.IPostService;
@Service
public class PostService implements IPostService {
	@Autowired
	private PostRepository postRep;
	@Autowired 
	private GroupMemberRepository groupMemberRep;
	@Autowired
	private GroupRepository groupRep;
	private Logger logger = LoggerFactory.getLogger(PostService.class);
	@Override
	public List<PostDto> findByUserId(Long id) {
		List<PostDto> listPost = new LinkedList<PostDto>();
		postRep.findByUserId(id).forEach((post)-> {
			GalleryDto media = post.getMedia()!=null? new GalleryDto(post.getId(),post.getMedia().getMedia(),post.getMedia().getMedia_type()):null;
			GroupMediaDto groupMedia = post.getGroup_media()!=null? new GroupMediaDto(post.getGroup_media().getId(),post.getGroup_media().getMedia(),post.getGroup_media().getMedia_type()):null;
			String sharedFullName = null;
		    String sharedUsername = null;
		    String sharedGender = null;
		    LocalDateTime sharedCreatedAt = null;
		    if (post.getPostshare() != null) {
		        sharedFullName = post.getPostshare().getUser().getFullname();
		        sharedUsername = post.getPostshare().getUser().getUsername();
		        sharedGender = post.getPostshare().getUser().getGender();
		        sharedCreatedAt = post.getPostshare().getUser().getCreate_at();
		    }
			listPost.add(new PostDto(
								post.getId(), 
								post.getText(), 
								media,
								groupMedia,
								post.getGroup()!=null? post.getGroup().getId(): null, 
								post.getCreate_at(),
								post.getUser().getFullname(),
								post.getUser().getUsername(),
								post.getUser().getImage()!=null?post.getUser().getImage().getMedia():null,
								post.getUser().getGender(),
								post.getGroup()!=null?post.getGroup().getGroupname():null,
								post.getGroup()!=null?post.getGroup().isPublic_group():false,
								post.getGroup()!=null && post.getGroup().getImage_group()!=null?post.getGroup().getImage_group().getMedia():null,
								post.getUser_setting()!=null?post.getUser_setting().getSetting_type().toString():ESettingType.PUBLIC.toString(),
								post.getColor(),
						        post.getBackground(),
						        post.getUser().getId(),
						        post.getShare(),
				                post.getMedias(),
				                sharedFullName,
				                sharedUsername,
				                sharedGender,
				                sharedCreatedAt,
				                post.is_blocked()
								));

		});
		return listPost;
	}
	@Override
	public List<PostDto> newestPostsForUser(Long id, int page, int limit) {
	    List<PostDto> listPost = new LinkedList<PostDto>();
	    int offset = page * limit;
	    postRep.getNewestPostFromGroupAndFriend(id, limit, offset).forEach((post) -> {
	        GalleryDto media = post.getMedia() != null ? new GalleryDto(post.getId(), post.getMedia().getMedia(), post.getMedia().getMedia_type()) : null;
	        GroupMediaDto groupMedia = post.getGroup_media() != null ? new GroupMediaDto(post.getGroup_media().getId(), post.getGroup_media().getMedia(), post.getGroup_media().getMedia_type()) : null;
	        String sharedFullName = null;
	        String sharedUsername = null;
	        String sharedGender = null;
	        LocalDateTime sharedCreatedAt = null;
	        if (post.getPostshare() != null) {
	            sharedFullName = post.getPostshare().getUser().getFullname();
	            sharedUsername = post.getPostshare().getUser().getUsername();
	            sharedGender = post.getPostshare().getUser().getGender();
	            sharedCreatedAt = post.getPostshare().getUser().getCreate_at();
	        }
	        listPost.add(new PostDto(
	                post.getId(),
	                post.getText(),
	                media,
	                groupMedia,
	                post.getGroup() != null ? post.getGroup().getId() : null,
	                post.getCreate_at(),
	                post.getUser().getFullname(),
	                post.getUser().getUsername(),
	                post.getUser().getImage() != null ? post.getUser().getImage().getMedia() : null,
	                post.getUser().getGender(),
	                post.getGroup() != null ? post.getGroup().getGroupname() : null,
	                post.getGroup() != null ? post.getGroup().isPublic_group() : false,
	                post.getGroup() != null && post.getGroup().getImage_group() != null ? post.getGroup().getImage_group().getMedia() : null,
	                post.getUser_setting() != null ? post.getUser_setting().getSetting_type().toString() : ESettingType.PUBLIC.toString(),
	                post.getColor(),
	                post.getBackground(),
	                post.getUser().getId(),
	                post.getShare(),
	                post.getMedias(),
	                sharedFullName,
	                sharedUsername,
	                sharedGender,
	                sharedCreatedAt,
            		post.is_blocked()
					));});
		return listPost;
	}
	@Override
	public List<PostDto> getSearchPosts(String search, Long id) {
		List<PostDto> listPost = new LinkedList<PostDto>();
		postRep.searchPublicPostsContain(search, id).forEach((post)->{
			GalleryDto media = post.getMedia()!=null? new GalleryDto(post.getId(),post.getMedia().getMedia(),post.getMedia().getMedia_type()):null;
			GroupMediaDto groupMedia = post.getGroup_media()!=null? new GroupMediaDto(post.getGroup_media().getId(),post.getGroup_media().getMedia(),post.getGroup_media().getMedia_type()):null;
			String sharedFullName = null;
	        String sharedUsername = null;
	        String sharedGender = null;
	        LocalDateTime sharedCreatedAt = null;
	        if (post.getPostshare() != null) {
	            sharedFullName = post.getPostshare().getUser().getFullname();
	            sharedUsername = post.getPostshare().getUser().getUsername();
	            sharedGender = post.getPostshare().getUser().getGender();
	            sharedCreatedAt = post.getPostshare().getUser().getCreate_at();
	        }
			listPost.add( new PostDto(
					post.getId(), 
					post.getText(), 
					media,
					groupMedia,
					post.getGroup()!=null? post.getGroup().getId(): null, 
					post.getCreate_at(),
					post.getUser().getFullname(),
					post.getUser().getUsername(),
					post.getUser().getImage()!=null? post.getUser().getImage().getMedia():null,
					post.getUser().getGender(),
					post.getGroup()!=null?post.getGroup().getGroupname():null,
					post.getGroup()!=null?post.getGroup().isPublic_group():false,
					post.getGroup()!=null && post.getGroup().getImage_group() !=null?post.getGroup().getImage_group().getMedia():null,
					post.getUser_setting()!=null?post.getUser_setting().getSetting_type().toString():ESettingType.PUBLIC.toString(),
					post.getColor(),
					post.getBackground(),
					post.getUser().getId(),
					post.getShare(),
	                post.getMedias(),
	                sharedFullName,
	                sharedUsername,
	                sharedGender,
	                sharedCreatedAt,
	        		post.is_blocked()
					));});
		postRep.searchPostsInGroupJoinedContain(search, id).forEach((post)->{
			GalleryDto media = post.getMedia()!=null? new GalleryDto(post.getId(),post.getMedia().getMedia(),post.getMedia().getMedia_type()):null;
			GroupMediaDto groupMedia = post.getGroup_media()!=null? new GroupMediaDto(post.getGroup_media().getId(),post.getGroup_media().getMedia(),post.getGroup_media().getMedia_type()):null;
			String sharedFullName = null;
	        String sharedUsername = null;
	        String sharedGender = null;
	        LocalDateTime sharedCreatedAt = null;
	        if (post.getPostshare() != null) {
	            sharedFullName = post.getPostshare().getUser().getFullname();
	            sharedUsername = post.getPostshare().getUser().getUsername();
	            sharedGender = post.getPostshare().getUser().getGender();
	            sharedCreatedAt = post.getPostshare().getUser().getCreate_at();
	        }
			listPost.add( new PostDto(
					post.getId(), 
					post.getText(), 
					media,
					groupMedia,
					post.getGroup()!=null? post.getGroup().getId(): null, 
					post.getCreate_at(),
					post.getUser().getFullname(),
					post.getUser().getUsername(),
					post.getUser().getImage()!=null? post.getUser().getImage().getMedia():null,
					post.getUser().getGender(),
					post.getGroup()!=null?post.getGroup().getGroupname():null,
					post.getGroup()!=null?post.getGroup().isPublic_group():false,
					post.getGroup()!=null && post.getGroup().getImage_group()!=null?post.getGroup().getImage_group().getMedia():null,
					post.getUser_setting()!=null?post.getUser_setting().getSetting_type().toString():ESettingType.PUBLIC.toString(),
					post.getColor(),
					post.getBackground(),
					post.getUser().getId(),
					post.getShare(),
	                post.getMedias(),
	                sharedFullName,
	                sharedUsername,
	                sharedGender,
	                sharedCreatedAt,
	        		post.is_blocked()
					));});
		return listPost;
	}
	//Group posts
	@Override
	public List<PostDto> newestPostInGroup(Long id_group, Long id_user, int limit, int page) {
		List<PostDto> list = new LinkedList<PostDto>();
		Optional<GroupMember> groupMem = groupMemberRep.findMemberInGroupWithUser(id_group, id_user);
		Optional<Group> group = groupRep.findById(id_group);
		if( groupMem.isPresent()  || (group.get().isActive()&& group.get().isPublic_group())) {
			int offset = page* limit;
			postRep.getNewestPostFromGroup(id_group, id_user, limit,offset).forEach((post)->{
				GroupMediaDto groupMedia = post.getGroup_media()!=null? new GroupMediaDto(post.getGroup_media().getId(),post.getGroup_media().getMedia(),post.getGroup_media().getMedia_type()):null;
				String sharedFullName = null;
		        String sharedUsername = null;
		        String sharedGender = null;
		        LocalDateTime sharedCreatedAt = null;
		        if (post.getPostshare() != null) {
		            sharedFullName = post.getPostshare().getUser().getFullname();
		            sharedUsername = post.getPostshare().getUser().getUsername();
		            sharedGender = post.getPostshare().getUser().getGender();
		            sharedCreatedAt = post.getPostshare().getUser().getCreate_at();
		        }
				list.add(new PostDto(
						post.getId(), 
						post.getText(), 
						null,
						groupMedia,
						id_group, 
						post.getCreate_at(), 
						post.getUser().getFullname(),
						post.getUser().getUsername(), 
						post.getUser().getImage()!=null? post.getUser().getImage().getMedia():null, 
						post.getUser().getGender(), 
						post.getGroup().getGroupname(), 
						post.getGroup().isPublic_group(), 
						post.getGroup().getImage_group()!=null? post.getGroup().getImage_group().getMedia():null, 
						post.getUser_setting()!=null? post.getUser_setting().getSetting_type().toString(): ESettingType.PUBLIC.toString(),
						post.getColor(),
						post.getBackground(),
						post.getUser().getId(),
						post.getShare(),
		                post.getMedias(),
		                sharedFullName,
		                sharedUsername,
		                sharedGender,
		                sharedCreatedAt,
    	        		post.is_blocked()
						));
			});;
			
		}
		return list;
	}
	@Override
	public List<PostDto> getPostsForUser(Long id, int page, int limit) {
		List<PostDto> listPost = new LinkedList<PostDto>();
		int firsIndex = page*limit <getAllPostForUser(id).size() ?page* limit :-1;
		int lastIndex = (page*limit) +5 <=getAllPostForUser(id).size()?(page*limit) +5 :getAllPostForUser(id).size();
		if(firsIndex>=0) {
			listPost = getAllPostForUser(id).subList(firsIndex, lastIndex);			
		}
		return listPost;
	}
	@Override
	public List<PostDto> getAllPostForUser(Long id) {
		List<PostDto> listPost = new LinkedList<PostDto>();
		postRep.getAllPostsFromGroupAndFriend(id).forEach((post)->{
			GalleryDto media = post.getMedia()!=null? new GalleryDto(post.getId(),post.getMedia().getMedia(),post.getMedia().getMedia_type()):null;
			GroupMediaDto groupMedia = post.getGroup_media()!=null? new GroupMediaDto(post.getGroup_media().getId(),post.getGroup_media().getMedia(),post.getGroup_media().getMedia_type()):null;
			String sharedFullName = null;
	        String sharedUsername = null;
	        String sharedGender = null;
	        LocalDateTime sharedCreatedAt = null;
	        if (post.getPostshare() != null) {
	            sharedFullName = post.getPostshare().getUser().getFullname();
	            sharedUsername = post.getPostshare().getUser().getUsername();
	            sharedGender = post.getPostshare().getUser().getGender();
	            sharedCreatedAt = post.getPostshare().getUser().getCreate_at();
	        }
			listPost.add( new PostDto(
					post.getId(), 
					post.getText(), 
					media,
					groupMedia,
					post.getGroup()!=null? post.getGroup().getId(): null, 
					post.getCreate_at(),
					post.getUser().getFullname(),
					post.getUser().getUsername(),
					post.getUser().getImage()!=null? post.getUser().getImage().getMedia():null,
					post.getUser().getGender(),
					post.getGroup()!=null?post.getGroup().getGroupname():null,
					post.getGroup()!=null?post.getGroup().isPublic_group():false,
					post.getGroup()!=null && post.getGroup().getImage_group()!=null?post.getGroup().getImage_group().getMedia():null,
					post.getUser_setting()!=null?post.getUser_setting().getSetting_type().toString():ESettingType.PUBLIC.toString(),
					post.getColor(),
					post.getBackground(),
					post.getUser().getId(),
					post.getShare(),
	                post.getMedias(),
	                sharedFullName,
	                sharedUsername,
	                sharedGender,
	                sharedCreatedAt,
	        		post.is_blocked()

					));});
		return listPost;
	}
	@Override
	public Optional<PostDto> getPost(Long id_user, Long id_post) {
		Optional<Post> findPost = postRep.getPostQuery(id_user, id_post);
		if(findPost.isPresent()) {
			Post post = findPost.get();
			GalleryDto media = post.getMedia()!=null? new GalleryDto(post.getId(),post.getMedia().getMedia(),post.getMedia().getMedia_type()):null;
			GroupMediaDto groupMedia = post.getGroup_media()!=null? new GroupMediaDto(post.getGroup_media().getId(),post.getGroup_media().getMedia(),post.getGroup_media().getMedia_type()):null;
			String sharedFullName = null;
	        String sharedUsername = null;
	        String sharedGender = null;
	        LocalDateTime sharedCreatedAt = null;
	        if (post.getPostshare() != null) {
	            sharedFullName = post.getPostshare().getUser().getFullname();
	            sharedUsername = post.getPostshare().getUser().getUsername();
	            sharedGender = post.getPostshare().getUser().getGender();
	            sharedCreatedAt = post.getPostshare().getUser().getCreate_at();
	        }
	        PostDto getPost = new PostDto(
					post.getId(), 
					post.getText(), 
					media,
					groupMedia,
					post.getGroup()!=null? post.getGroup().getId(): null, 
					post.getCreate_at(),
					post.getUser().getFullname(),
					post.getUser().getUsername(),
					post.getUser().getImage()!=null? post.getUser().getImage().getMedia():null,
					post.getUser().getGender(),
					post.getGroup()!=null?post.getGroup().getGroupname():null,
					post.getGroup()!=null?post.getGroup().isPublic_group():false,
					post.getGroup()!=null && post.getGroup().getImage_group()!=null?post.getGroup().getImage_group().getMedia():null,
					post.getUser_setting()!=null?post.getUser_setting().getSetting_type().toString():ESettingType.PUBLIC.toString(),
					post.getColor(),
					post.getBackground(),
					post.getUser().getId(),
					post.getShare(),
	                post.getMedias(),
	                sharedFullName,
	                sharedUsername,
	                sharedGender,
	                sharedCreatedAt,
	        		post.is_blocked()
					);
	        return Optional.of(getPost);
		}
		return null;
	}
}
