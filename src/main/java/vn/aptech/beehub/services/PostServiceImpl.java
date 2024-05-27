package vn.aptech.beehub.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;


import vn.aptech.beehub.aws.S3Service;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.PostMeDto;
import vn.aptech.beehub.models.LikeUser;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.repository.LikeRepository;
import vn.aptech.beehub.repository.PostCommentRepository;
import vn.aptech.beehub.repository.PostReactionRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.UserRepository;

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
	private S3Service s3Service;
	
	@Autowired
	private ModelMapper mapper;
	
	public List<Post> findAllPost() {
		Sort sortByCreatedAtDesc = Sort.by(Sort.Direction.DESC,"createdAt");
		List<Post> posts = postRepository.findAll(sortByCreatedAtDesc);
		return posts;
	}
	 
	public Post savePost(PostMeDto dto) { 
		Post post = mapper.map(dto, Post.class);
		post.setCreate_at(LocalDateTime.now());
		if(dto.getColor() == null || dto.getColor().isEmpty()) {
			post.setColor("inherit");
		}
		if(dto.getBackground() == null || dto.getBackground().isEmpty()) {
			post.setBackground("inherit");
		}
		if(dto.getUser() > 0) {
			userRepository.findById(dto.getUser()).ifPresent(post::setUser); 
		}
		post.setMedias(dto.getMediaUrl());
		Post saved = postRepository.save(post); 
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
		        
		        post.setMedias(dto.getMediaUrl());
			Post postUpdate = postRepository.save(post);
			return postUpdate;
		}
		 else {
		    throw new RuntimeException("Post not found with id " + dto.getId());
		 } 	
	}
	
	public Optional<Post> findByIdPost(Long id) {
		return postRepository.findById(id);
	}

	public List<User> findAllUser(){
		return userRepository.findAll();
	}
}
