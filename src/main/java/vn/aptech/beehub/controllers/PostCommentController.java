package vn.aptech.beehub.controllers;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
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
import vn.aptech.beehub.dto.PostCommentDto;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.services.PostCommentService;

@Tag(name = "Comment")
@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostCommentController {
	@Autowired
	private PostCommentService postCommentService;
	
	@GetMapping(value = "/comment/{id}")
	public ResponseEntity<List<PostComment>> findCommentByPostId(@PathVariable("id") int id){
		List<PostComment> result = postCommentService.findCommentById(id);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping(value = "/comment/create")
	public ResponseEntity<PostComment>create(@RequestBody @Validated PostCommentDto dto){
		return ResponseEntity.ok(postCommentService.saveComment(dto));
	}
}
