package vn.aptech.beehub.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.aptech.beehub.dto.PostCommentDto;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.repository.PostCommentRepository;
import vn.aptech.beehub.repository.PostReactionRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.UserRepository;
@Service
public class PostCommentServiceImpl implements PostCommentService {
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private PostCommentRepository postCommentRepository;
	
	@Autowired
	private PostReactionRepository postReactionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	public List<PostComment> findCommentById(int id){
		return postCommentRepository.findCommentById(id);
	}
	public Optional<PostComment> findCommenyById(int id) {
		return postCommentRepository.findById(id);
	}
	public PostComment saveComment(PostCommentDto dto) {
		PostComment comment = mapper.map(dto,PostComment.class);
		comment.setCreatedAt(LocalDateTime.now());
		if(dto.getPost() > 0) {
			postRepository.findById(dto.getPost()).ifPresent(comment::setPost);
		}
		if(dto.getUser() > 0) {
			userRepository.findById(dto.getUser()).ifPresent(comment::setUser); 
		}
		PostComment saved = postCommentRepository.save(comment);
		return saved;
	}
	public PostComment editComment(PostCommentDto dto) {
		Optional<PostComment> optionalComment = postCommentRepository.findById(dto.getId());
		if(optionalComment.isPresent()) {
			PostComment postComment = optionalComment.get();
			if(dto.getPost() > 0) {
				postRepository.findById(dto.getPost()).ifPresent(postComment::setPost);
			}
			if(dto.getUser() > 0) {
				userRepository.findById(dto.getUser()).ifPresent(postComment::setUser); 
			}
			postComment.setComment(dto.getComment());
			PostComment saved = postCommentRepository.save(postComment);
			return saved;
		}else {
		    throw new RuntimeException("PostComment not found with id " + dto.getId());
		}
	}
	public boolean deleteComment(int id) {
        Optional<PostComment> optionalPostComment = postCommentRepository.findById(id);
        if (optionalPostComment.isPresent()) {
            PostComment postComment = optionalPostComment.get();
            List<PostReaction> recomment = postComment.getReactions();
            postReactionRepository.deleteAll(recomment);
            postCommentRepository.deleteById(id);
            return true;

        }
        return false;
    }
}
