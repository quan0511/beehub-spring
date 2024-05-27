package vn.aptech.beehub.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.aptech.beehub.dto.PostReactionDto;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.services.PostReactionService;

@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostReactionController {
	@Autowired
	private PostReactionService postReactionService;
	
	@GetMapping(value = "/recomment/{id}")
	public ResponseEntity<List<PostReaction>> findReCommentByPostId(@PathVariable("id") int id){
		List<PostReaction> result = postReactionService.findRecommentByComment(id);
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
}
