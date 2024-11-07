package vn.aptech.beehub.controllers;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.aptech.beehub.dto.PostCommentDto;
import vn.aptech.beehub.dto.PostMeDto;
import vn.aptech.beehub.dto.PostReactionDto;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.services.PostCommentService;

@Tag(name = "Comment")
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "https://beehub-spring.onrender.com", maxAge = 3600, allowCredentials = "true")
@Slf4j
public class PostCommentController {
	@Autowired
	private PostCommentService postCommentService;
	
	@GetMapping(value = "/comment/{id}")
	public ResponseEntity<List<PostCommentDto>> findCommentByPostId(@PathVariable("id") int id){
		List<PostCommentDto> result = postCommentService.findCommentById(id).stream().map((p) ->
				PostCommentDto.builder()
						.id(p.getId())
						.comment(p.getComment())
						.post(p.getPost().getId())
						.user(p.getUser().getId())
						.username(p.getUser().getUsername())
						.fullname(p.getUser().getFullname())
						.usergender(p.getUser().getGender())
						.userimage(p.getUser().getImage()!= null ? p.getUser().getImage().getMedia() : null)
						.createdAt(p.getCreatedAt())
						.build()).toList();
		return ResponseEntity.ok(result);
	}
	@GetMapping(value = "/commentpost/{id}")
	public ResponseEntity<PostCommentDto> findCommentById(@PathVariable("id") int id){
	    Optional<PostComment> optionalPost = postCommentService.findCommenyById(id);
	    if (!optionalPost.isPresent()) {
	        return ResponseEntity.notFound().build();
	    }
	    PostComment p = optionalPost.get();
	    PostCommentDto post = PostCommentDto.builder()
	                              .id(p.getId())
	                              .comment(p.getComment())
	                              .post(p.getPost().getId())
	                              .user(p.getUser().getId())
	                              .build();
	    return ResponseEntity.ok(post);
	}
	
	@PostMapping(value = "/comment/create")
	public ResponseEntity<PostComment>create(@RequestBody @Validated PostCommentDto dto){
		return ResponseEntity.ok(postCommentService.saveComment(dto));
	}
	@PostMapping(value = "/comment/edit")
	public ResponseEntity<PostCommentDto>updatePostComment(@RequestBody @Validated PostCommentDto dto){ 
		PostComment updatedComment = postCommentService.editComment(dto);
	    PostCommentDto updatedDto = PostCommentDto.builder()
	            .id(updatedComment.getId())
	            .comment(updatedComment.getComment())
	            .user(updatedComment.getUser().getId())
	            .post(updatedComment.getPost().getId())
	            .username(updatedComment.getUser().getUsername())
	            .createdAt(updatedComment.getCreatedAt())
	            .build();
	    return ResponseEntity.ok(updatedDto);  
	}
	@PostMapping(value = "/comment/delete/{id}")
	public ResponseEntity<Boolean>deletePostComment(@PathVariable("id") int id){
		return ResponseEntity.ok(postCommentService.deleteComment(id));
	}
	@GetMapping(value = "/comment/post/{postid}")
	public ResponseEntity<Integer> count(@PathVariable("postid") Long postid){
		return ResponseEntity.ok(postCommentService.CountCommentByPost(postid));
	}
}
