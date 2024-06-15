package vn.aptech.beehub.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.amazonaws.SdkClientException;
import jakarta.transaction.Transactional;
import vn.aptech.beehub.aws.S3Service;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.PostMeDto;
import vn.aptech.beehub.models.ESettingType;
import vn.aptech.beehub.models.Gallery;
import vn.aptech.beehub.models.GroupMedia;
import vn.aptech.beehub.models.LikeUser;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.models.RelationshipUsers;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.models.UserSetting;
import vn.aptech.beehub.repository.GalleryRepository;
import vn.aptech.beehub.repository.GroupMediaRepository;
import vn.aptech.beehub.repository.GroupRepository;
import vn.aptech.beehub.repository.LikeRepository;
import vn.aptech.beehub.repository.PostCommentRepository;
import vn.aptech.beehub.repository.PostReactionRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.RelationshipUsersRepository;
import vn.aptech.beehub.repository.ReportRepository;
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
	private PostReactionRepository postReactionRepository;
	
	@Autowired
	private GalleryRepository galleryRepository;
	
	@Autowired
	private UserSettingRepository userSettingRepository;
	
	@Autowired
	private GroupMediaRepository groupMediaRep;
	
	@Autowired
	private ReportRepository reportRep;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private RelationshipUsersRepository relationshipUsersRepository;
	
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
	    Long userId = dto.getUser() != null && dto.getUser() > 0 ? dto.getUser() : 1L;
	    userRepository.findById(userId).ifPresent(post::setUser);
	    
	    if (dto.getGroup() != null && dto.getGroup() > 0) {
	        groupRepository.findById(dto.getGroup()).ifPresent(post::setGroup);
	    } else {
	        dto.setGroup(null);
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
	    
	    if (post.getGroup() != null && post.getMedias() != null && !post.getMedias().isEmpty()) {
	        GroupMedia groupMedia = new GroupMedia();
	        groupMedia.setMedia(post.getMedias());
	        groupMedia.setMedia_type("image");
	        groupMedia.setCreate_at(LocalDateTime.now());
	        groupMedia.setUser(post.getUser());
	        groupMedia.setGroup(post.getGroup());
	        groupMedia.setPost(saved);
	        
	        groupMediaRep.save(groupMedia);
	    }
	    
	    return saved;
	}
	@Transactional
	public boolean deletePost(Long id) {
	    Optional<Post> optionalPost = postRepository.findById(id);
	    if (optionalPost.isPresent()) {
	        Post post = optionalPost.get();
	        //post.setUser(null);
	        String filename = post.getMedias();
	        String fileExtract = extractFileNameFromUrl(filename);
	        try {
	            if (filename != null && !filename.isEmpty()) {
	                s3Service.deleteToS3(fileExtract);
	            }
	            if(post.getGroup_media()!=null) {
	            	String filename2 = post.getGroup_media().getMedia();
	            	String fileExtract2 = filename2!=null? filename2.substring(filename2.lastIndexOf("/") + 1):null;
	            	if(fileExtract !=null) {
	            		s3Service.deleteToS3(fileExtract);
	            	}
	            	GroupMedia gallery = post.getGroup_media();
	            	groupMediaRep.delete(gallery);				            	
	            }
	            
	            reportRep.deletePostReposts(post.getId());
	            if(post.getMedias() != null) {
	            	
	            	postRepository.deletePostReactions(id);
	            	postRepository.deletePostComments(id);
	            	postRepository.deletePostLikes(id);
	            	postRepository.deletePostReports(id);
	            	deletePostByPostShare(post.getId());
	            	postRepository.deletePostWithGallery(id);
	            	postRepository.deletePost(id);
	            	postRepository.deleteUserSettings(id);           	
	            }else {            	
	            	postRepository.deletePostReactions(id);
	            	postRepository.deletePostComments(id);
	            	postRepository.deletePostLikes(id);
	            	postRepository.deletePostReports(id);	     
	            	deletePostByPostShare(post.getId());
	            	postRepository.deletePost(id);
	            	postRepository.deleteUserSettings(id);
	            }
	            
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
	private void deletePostByPostShare(Long id) {
	    List<Post> posts = postRepository.findByPostshareId(id);
	    for (Post post : posts) {
	        postRepository.deletePostReactions(post.getId());
	        postRepository.deletePostComments(post.getId());
	        postRepository.deletePostLikes(post.getId());
	        postRepository.deletePostReports(post.getId());
	        postRepository.deletePostBypostshare(id);
	        postRepository.deleteUserSettings(post.getId());
	    }
	}
	private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
	public Post updatePost(PostMeDto dto) {
	    Optional<Post> optionalPost = postRepository.findById(dto.getId());
	    if (optionalPost.isPresent()) {
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
	        if (dto.getMediaUrl()!= fileOld && fileOld != null) {
	            s3Service.deleteToS3(fileOldEx);
	        }
	        if (dto.getGroup() != null && dto.getGroup() > 0) {
		        groupRepository.findById(dto.getGroup()).ifPresent(post::setGroup);
		    } else {
		        dto.setGroup(null);
		    }
	        if (dto.getMediaUrl() != null && !dto.getMediaUrl().isEmpty()) {
	            post.setMedias(dto.getMediaUrl());
	            if (post.getMedia() != null) {
	                updateGalleryMedias(post, dto.getMediaUrl());
	            } else {
	                Gallery gallery = new Gallery();
	                gallery.setUser(post.getUser()); 
	                gallery.setPost(post); 
	                gallery.setMedia(post.getMedias()); 
	                gallery.setMedia_type("image");
	                gallery.setCreate_at(LocalDateTime.now());
	                galleryRepository.save(gallery);
	            }
	        }   
	        
	        Post postUpdate = postRepository.save(post);
	        List<Post> relatedPosts = postRepository.findByPostshareId(post.getId());
	        for (Post relatedPost : relatedPosts) {
	            relatedPost.setText(post.getText());
	            relatedPost.setColor(post.getColor());
	            relatedPost.setBackground(post.getBackground());
	            relatedPost.setMedias(post.getMedias());
	            postRepository.save(relatedPost);
	        }
	        
	        return postUpdate;
	    } else {
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
		Post post = optionalPost.get();
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
		sharePost.setPostshare(post);
		return postRepository.save(sharePost);
	}
	public int countShareByPostId(Long id) {
		List<Post> share = postRepository.findByPostshareId(id);
		return share.size();

	}
	public List<User> findUser(Long id){
		List<Long> rela = relationshipUsersRepository.findFriendsByUser(id);
		List<User> user = userRepository.findAllById(rela);
		return userRepository.findAllById(rela);
	}
}
