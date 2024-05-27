package vn.aptech.beehub.services;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.aptech.beehub.dto.PostCommentDto;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.repository.PostCommentRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.UserRepository;
@Service
public class PostCommentServiceImpl implements PostCommentService {
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private PostCommentRepository postCommentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	public List<PostComment> findCommentById(int id){
		return postCommentRepository.findCommentById(id);
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
}
