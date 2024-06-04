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
import vn.aptech.beehub.dto.PostReactionDto;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.services.PostReactionService;

@Tag(name = "Reaction")
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
@Slf4j
public class PostReactionController {
	@Autowired
	private PostReactionService postReactionService;
	
	@GetMapping(value = "/recomment/{id}")
	public ResponseEntity<List<PostReactionDto>> findReCommentByPostId(@PathVariable("id") int id){
		List<PostReactionDto> result = postReactionService.findRecommentByComment(id).stream().map((p) ->
		PostReactionDto.builder()
				.id(p.getId())
				.reaction(p.getReaction())
				.user(p.getUser().getId())
				.post(p.getPost().getId())
				.postComment(p.getPostComment().getId())
				.username(p.getUser().getUsername())
				.createdAt(p.getCreatedAt())
				.build()).toList();
return ResponseEntity.ok(result);
	}
	@PostMapping(value = "/recomment/create")
	public ResponseEntity<PostReaction>create(@RequestBody @Validated PostReactionDto dto){
		return ResponseEntity.ok(postReactionService.saveRecomment(dto));
	}
	@GetMapping(value = "/recomment/comment/{commentid}")
	public ResponseEntity<Integer> countReaction(@PathVariable("commentid") int commentid){
		return ResponseEntity.ok(postReactionService.countReactionByComment(commentid));
	}
	@PostMapping(value = "/recomment/update")
	public ResponseEntity<PostReactionDto> updatePostReaction(@RequestBody @Validated PostReactionDto dto){ 
	    PostReaction updatedReaction = postReactionService.editRecomment(dto);
	    PostReactionDto updatedDto = PostReactionDto.builder()
	            .id(updatedReaction.getId())
	            .reaction(updatedReaction.getReaction())
	            .user(updatedReaction.getUser().getId())
	            .post(updatedReaction.getPost().getId())
	            .postComment(updatedReaction.getPostComment().getId())
	            .username(updatedReaction.getUser().getUsername())
	            .createdAt(updatedReaction.getCreatedAt())
	            .build();
	    return ResponseEntity.ok(updatedDto); 
	}
	@PostMapping(value = "/recomment/delete/{id}")
	public ResponseEntity<Boolean>deletePostReaction(@PathVariable("id") int id){
		return ResponseEntity.ok(postReactionService.deletePostReaction(id));
	}
	@GetMapping(value = "recommentpost/{id}")
	public ResponseEntity<PostReactionDto> findReactionById(@PathVariable("id") int id){
		Optional<PostReaction> result = postReactionService.findReactionById(id);
		if(!result.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		PostReaction p = result.get();
		PostReactionDto reaction = PostReactionDto.builder()
				.id(p.getId())
				.reaction(p.getReaction())
				.createdAt(p.getCreatedAt())
				.post(p.getPost().getId())
				.user(p.getUser().getId())
				.postComment(p.getPostComment().getId())
				.build();
		return ResponseEntity.ok(reaction);
	}
}
