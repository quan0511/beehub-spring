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
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.models.UserSetting;
import vn.aptech.beehub.repository.GalleryRepository;
import vn.aptech.beehub.repository.LikeRepository;
import vn.aptech.beehub.repository.PostCommentRepository;
import vn.aptech.beehub.repository.PostReactionRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.RelationshipUsersRepository;
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
	public List<RelationshipUsers> findUserByUser(Long id){
		List<RelationshipUsers> rela = relationshipUsersRepository.findFriendsOfUser1(id);
		return rela;
	}
	 
	public Post savePost(PostMeDto dto) {
		logger.info(dto.getUser().toString());
		System.out.println(dto.getUser().toString());
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
	    
	    // Create and save UserSetting
	    UserSetting userSetting = new UserSetting();
	    userSetting.setSetting_type(ESettingType.PUBLIC);
	    userSetting.setUser(post.getUser());
	    userSetting = userSettingRepository.save(userSetting);
	    
	    // Associate the UserSetting with the Post
	    post.setUser_setting(userSetting);
	    
	    Post saved = postRepository.save(post);
	    
	    // Save gallery if media URL is provided
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
                List<PostComment> comment = post.getComments();
                postCommentRepository.deleteAll(comment);
                List<PostReaction> recomment = post.getReactions();
                postReactionRepository.deleteAll(recomment);
                List<LikeUser> like = post.getLikes();
                likeRepository.deleteAll(like);
                List<Gallery> gallery = post.getGallerys();
                galleryRepository.deleteAll(gallery);
                postRepository.deleteById(id);
                return true;
            } catch (SdkClientException e) {
                e.printStackTrace();
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
	
}
