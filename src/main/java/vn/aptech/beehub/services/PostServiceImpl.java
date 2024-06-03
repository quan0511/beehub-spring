package vn.aptech.beehub.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;

import jakarta.transaction.Transactional;
import vn.aptech.beehub.aws.S3Service;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.PostMeDto;
import vn.aptech.beehub.models.ESettingType;
import vn.aptech.beehub.models.Gallery;
import vn.aptech.beehub.models.LikeUser;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.models.RelationshipUsers;
import vn.aptech.beehub.models.SharePost;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.models.UserSetting;
import vn.aptech.beehub.repository.GalleryRepository;
import vn.aptech.beehub.repository.LikeRepository;
import vn.aptech.beehub.repository.PostCommentRepository;
import vn.aptech.beehub.repository.PostReactionRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.RelationshipUsersRepository;
import vn.aptech.beehub.repository.ShareRepository;
import vn.aptech.beehub.repository.UserRepository;
import vn.aptech.beehub.repository.UserSettingRepository;
import vn.aptech.beehub.seeders.DatabaseSeeder;

@Service
public class PostServiceImpl implements PostService {
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private PostCommentRepository postCommentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired
	private ShareRepository shareRepository;
	
	@Autowired
	private PostReactionRepository postReactionRepository;
	
	@Autowired
	private GalleryRepository galleryRepository;
	
	@Autowired
	private UserSettingRepository userSettingRepository;
	
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private ModelMapper mapper;
	
	private Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
	
	public List<Post> findAllPost() {
		//Sort sortByCreatedAtDesc = Sort.by(Sort.Direction.DESC,"create_at");
		List<Post> posts = postRepository.findAll();
		return posts;
	}
	 
	public Post savePost(PostMeDto dto) {
	    Post post = mapper.map(dto, Post.class);
	    post.setCreate_at(LocalDateTime.now());
	    
	    if (dto.getColor() == null || dto.getColor().isEmpty()) {
	        post.setColor("inherit");
	    }
	    if (dto.getBackground() == null || dto.getBackground().isEmpty()) {
	        post.setBackground("inherit");
	    }
	    if (dto.getUser() > 0) {
	        userRepository.findById(dto.getUser()).ifPresent(post::setUser);
	    }
	    post.setMedias(dto.getMediaUrl());
	    
	    UserSetting userSetting = new UserSetting();
	    userSetting.setSetting_type(ESettingType.PUBLIC);
	    userSetting.setUser(post.getUser());
	    userSetting = userSettingRepository.save(userSetting);
	    
	    post.setUser_setting(userSetting);
	    Post saved = postRepository.save(post);
	    if (dto.getMediaUrl() != null && !dto.getMediaUrl().isEmpty()) {
		    Gallery gallery = new Gallery();
		    gallery.setUser(post.getUser()); 
		    gallery.setPost(saved); 
		    gallery.setMedia(saved.getMedias()); 
		    gallery.setMedia_type("image");
		    gallery.setCreate_at(LocalDateTime.now());
		    galleryRepository.save(gallery);
	    }
	    return saved;
	}
	@Transactional
	public boolean deletePost(Long id) {
	    Optional<Post> optionalPost = postRepository.findById(id);
	    if (optionalPost.isPresent()) {
	        Post post = optionalPost.get();
	        String filename = post.getMedias();
	        String fileExtract = extractFileNameFromUrl(filename);
	        try {
	            if (filename != null && !filename.isEmpty()) {
	                s3Service.deleteToS3(fileExtract);
	            }
	            List<PostComment> comments = post.getComments();
	            postCommentRepository.deleteAll(comments);
	            List<PostReaction> reactions = post.getReactions();
	            postReactionRepository.deleteAll(reactions);
	            List<LikeUser> likes = post.getLikes();
	            likeRepository.deleteAll(likes);
	            List<Gallery> galleries = post.getGallerys();
	            galleryRepository.deleteAll(galleries);

	            // Ensure UserSetting deletion if not required elsewhere
	            UserSetting userSetting = post.getUser_setting();
	            if (userSetting != null) {
	                userSettingRepository.deleteById(userSetting.getId());
	            }

	            // Now delete the post itself
	            postRepository.deleteById(id);
	            return true;
	        } catch (SdkClientException e) {
	            logger.error("Error deleting media from S3", e);
	            return false;
	        } catch (Exception e) {
	            logger.error("Error deleting post", e);
	            return false;
	        }
	    }
	    return false;
	}
	private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
	public Post updatePost(PostMeDto dto) {
		Optional<Post> optionalPost = postRepository.findById(dto.getId());
		if(optionalPost.isPresent()) {
			Post post = optionalPost.get();
			String fileOld = post.getMedias();
			String fileOldEx = extractFileNameFromUrl(fileOld);
			post.setCreate_at(post.getCreate_at());
			 if (dto.getText() != null) {
		            post.setText(dto.getText());
		        }
		        if (dto.getColor() != null) {
		            post.setColor(dto.getColor());
		        } 
		        if (dto.getBackground() != null) {
		            post.setBackground(dto.getBackground());
		        }
		        if(fileOld != null && !fileOld.isEmpty()) {
		        	s3Service.deleteToS3(fileOldEx);
		        }
		        if (dto.getMediaUrl() != null && !dto.getMediaUrl().isEmpty()) {
		            post.setMedias(dto.getMediaUrl());
		            updateGalleryMedias(post, dto.getMediaUrl());
		        }
			Post postUpdate = postRepository.save(post);
			return postUpdate;
		}
		 else {
		    throw new RuntimeException("Post not found with id " + dto.getId());
		 } 	
	}
	private void updateGalleryMedias(Post post, String mediaUrl) {
	    Optional<Gallery> optionalGallery = galleryRepository.findByPost(post);
	    if (optionalGallery.isPresent()) {
	        Gallery gallery = optionalGallery.get();
	        gallery.setMedia(mediaUrl);
	        galleryRepository.save(gallery);
	    }
	}
	public Optional<Post> findByIdPost(Long id) {
		return postRepository.findById(id);
	}

	public List<User> findAllUser(){
		return userRepository.findAll();
	}
	public Post sharePost(PostMeDto dto) {
		Optional<Post> optionalPost = postRepository.findById(dto.getId());
		Optional<User> optionalUser = userRepository.findById(dto.getId());
		Post post = optionalPost.get();
		User user = optionalUser.get();
		User sharedUser = userRepository.findById(dto.getUser()).get();
		Post sharePost = new Post();
		sharePost.setText(post.getText());
		sharePost.setBackground(post.getBackground());
		sharePost.setColor(post.getColor());
		sharePost.setCreate_at(LocalDateTime.now());
		sharePost.setUser(sharedUser);
		sharePost.setMedias(post.getMedias());
		sharePost.setShare(true);
		sharePost.setTimeshare(post.getCreate_at());
		UserSetting userSetting = new UserSetting();
	    userSetting.setSetting_type(ESettingType.PUBLIC);
	    userSetting.setUser(sharePost.getUser());
	    userSetting = userSettingRepository.save(userSetting);
	    sharePost.setUser_setting(userSetting);
		SharePost share = new SharePost();
		share.setOriginalPost(post);
		share.setSharedBy(sharedUser);
		share.setSharedAt(sharePost.getTimeshare());
		share = shareRepository.save(share);
		sharePost.setPostshare(share);
		return postRepository.save(sharePost);
	}
//	public SharePost sharePost(Long postid, Long useid) {
//	    Optional<Post> optionalOriginalPost = postRepository.findById(postid);
//	    if (!optionalOriginalPost.isPresent()) {
//	        throw new RuntimeException("Original post not found with id " + postid);
//	    }
//	    Post originalPost = optionalOriginalPost.get();
//
//	    Optional<User> optionalSharedBy = userRepository.findById(useid);
//	    if (!optionalSharedBy.isPresent()) {
//	        throw new RuntimeException("User not found with id " + useid);
//	    }
//	    User sharedBy = optionalSharedBy.get();
//	    SharePost sharePost = new SharePost();
//	    sharePost.setOriginalPost(originalPost);
//	    sharePost.setSharedBy(sharedBy);
//	    sharePost.setSharedAt(LocalDateTime.now());
//	    shareRepository.save(sharePost);
//	    Post newPost = new Post();
//	    newPost.setUser(sharedBy);
//	    newPost.setText(originalPost.getText());
//	    newPost.setColor(originalPost.getColor());
//	    newPost.setBackground(originalPost.getBackground());
//	    newPost.setCreate_at(LocalDateTime.now());
//	    newPost.setMedias(originalPost.getMedias());
//	    newPost.setShare(sharePost.getId());
//	    newPost.setTimeshare(sharePost.getSharedAt());
//	    newPost = postRepository.save(newPost);
//	    return shareRepository.save(sharePost);
//	}
	
}
