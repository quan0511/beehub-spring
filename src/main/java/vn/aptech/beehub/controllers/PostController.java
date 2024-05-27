package vn.aptech.beehub.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.aptech.beehub.aws.S3Service;
import vn.aptech.beehub.controllers.PostController;
import vn.aptech.beehub.dto.PostDtoMe;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.services.PostService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostController {
	@Autowired
	private PostService postService;
	@Autowired
	private S3Service s3Service;
	@GetMapping
	public ResponseEntity<List<Post>> findAllPost() {
		List<Post> result = postService.findAllPost();
		return ResponseEntity.ok(result);
	}
	@GetMapping(value = "/user")
	public ResponseEntity<List<User>> findAllUser(){
		List<User> result = postService.findAllUser();
		return ResponseEntity.ok(result);
	}

	@PostMapping(value = "/create")
	public ResponseEntity<Post> create(@RequestParam(name= "media",required = false) MultipartFile media, @ModelAttribute @Validated PostDtoMe dto) {
	    try {
	        if (media != null && !media.isEmpty()) {
	            String fileUrl = s3Service.uploadToS3(media.getInputStream(), media.getOriginalFilename());
	            //String fileExtract = extractFileNameFromUrl(fileUrl);
	            dto.setMediaUrl(fileUrl);

	        }else {
	        	dto.setMediaUrl(null);
	        }
	        Post savedPost = postService.savePost(dto);
	        return ResponseEntity.ok(savedPost);
	    } catch (Exception e) {
	        log.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	@PostMapping(value = "/updatepost")
	public ResponseEntity<Post>update(@RequestParam(name= "media",required = false) MultipartFile media, @ModelAttribute @Validated PostDtoMe dto){
		try {
	        if (media == null && !media.isEmpty()) {
	            String fileUrl = s3Service.editToS3(media.getInputStream(), media.getOriginalFilename());
	            dto.setMediaUrl(fileUrl);

	        }else {
	        	dto.setMediaUrl(dto.getMediaUrl());
	        }
	        Post savedPost = postService.updatePost(dto);
	        return ResponseEntity.ok(savedPost);
	    } catch (Exception e) {
	        log.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	@PostMapping(value ="/deletepost/{id}")
	public ResponseEntity<Boolean> delete(@PathVariable("id") int id){
		return ResponseEntity.ok(postService.deletePost(id));
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<Post> findByIdPost(@PathVariable("id") int id){
		Optional<Post> result = postService.findByIdPost(id);
		if(result.isPresent()) {
			return ResponseEntity.ok(result.get());
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
}
