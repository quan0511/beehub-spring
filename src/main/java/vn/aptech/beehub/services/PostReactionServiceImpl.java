package vn.aptech.beehub.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.aptech.beehub.dto.PostReactionDto;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.repository.PostCommentRepository;
import vn.aptech.beehub.repository.PostReactionRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.UserRepository;
@Service
public class PostReactionServiceImpl implements PostReactionService {
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private PostCommentRepository postCommentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostReactionRepository postReactionRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	public List<PostReaction> findRecommentByComment(int id){
		return postReactionRepository.findReactionById(id);
	}
	
	public PostReaction saveRecomment(PostReactionDto dto) {
		PostReaction recomment = mapper.map(dto, PostReaction.class);
		recomment.setCreatedAt(LocalDateTime.now());
		if(dto.getPost() > 0) {
			postRepository.findById(dto.getPost()).ifPresent(recomment::setPost);
		}
		if(dto.getUser() > 0) {
			userRepository.findById(dto.getUser()).ifPresent(recomment::setUser); 
		}
		if(dto.getUser() > 0) {
			postCommentRepository.findById(dto.getPostComment()).ifPresent(recomment::setPostComment); 
		}
		PostReaction saved = postReactionRepository.save(recomment);
		return saved;
	}
	public int countReactionByComment(int commentId) {
		Optional<PostComment> optionalComment = postCommentRepository.findById(commentId);
		if(optionalComment.isPresent()) {
			PostComment comment = optionalComment.get();
			List<PostReaction> postReaction = postReactionRepository.findByPostComment(comment);
			return postReaction.size();
		}else {
			return 0;
		}
	}
}
