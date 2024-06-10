package vn.aptech.beehub.controllers;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.aptech.beehub.aws.S3Service;
import vn.aptech.beehub.controllers.PostController;
import vn.aptech.beehub.dto.PostCommentDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.PostMeDto;
import vn.aptech.beehub.dto.RelationshipUserDto;
import vn.aptech.beehub.dto.UserDto;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.payload.response.MessageResponse;
import vn.aptech.beehub.services.PostService;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Posts")
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
@Slf4j
public class PostController {
	private Logger logger = LoggerFactory.getLogger(PostController.class);
	
	@Autowired
	private PostService postService;
	@Autowired
	private S3Service s3Service;
	@Autowired
	private ModelMapper mapper;

	@GetMapping
	public ResponseEntity<List<Post>> findAllPost() {
		List<Post> result = postService.findAllPost();
		return ResponseEntity.ok(result);
	}
	@GetMapping(value = "/user")
	public ResponseEntity<List<UserDto>> findAllUser(){
		List<UserDto> result = postService.findAllUser().stream().map((r) ->
		UserDto.builder()
				.username(r.getUsername())
				
				.build()).toList();
		return ResponseEntity.ok(result);
	}

	@PostMapping(value = "/create")
	public ResponseEntity<?> create(@RequestParam(name= "medias",required = false) MultipartFile media, @ModelAttribute @Validated PostMeDto dto) {
		
		logger.info(dto.getUser().toString());
		System.out.println(dto.getUser().toString());
		try {
	        if (media != null && !media.isEmpty()) {
	            String fileUrl = s3Service.uploadToS3(media.getInputStream(), media.getOriginalFilename());
	            //String fileExtract = extractFileNameFromUrl(fileUrl);
	            dto.setMediaUrl(fileUrl);

	        }else {
	        	dto.setMediaUrl(null);
	        }
	        postService.savePost(dto);
	        return ResponseEntity.ok(dto);
	    } catch (Exception e) {
	        log.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	@PostMapping(value = "/updatepost")
	public ResponseEntity<PostMeDto>update(@RequestParam(name= "medias",required = false) MultipartFile media, @ModelAttribute @Validated PostMeDto dto){
		try {
	        if (media != null ) {
	            String fileUrl = s3Service.editToS3(media.getInputStream(), media.getOriginalFilename());
	            dto.setMediaUrl(fileUrl);
	        }
	        Post p = postService.updatePost(dto);
	        PostMeDto updatedDto = PostMeDto.builder()
		            .id(p.getId())
		            .text(p.getText())
		            .group(p.getGroup().getId())
		            .color(p.getColor())
		            .background(p.getBackground())
		            .createdAt(p.getCreate_at())
		            .user(p.getUser().getId())
		            .build();
		    return ResponseEntity.ok(updatedDto);
	    } catch (Exception e) {
	        log.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	@PostMapping(value ="/deletepost/{id}")
	public ResponseEntity<Boolean> delete(@PathVariable("id") Long id){
		return ResponseEntity.ok(postService.deletePost(id));
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<PostMeDto> findByIdPost(@PathVariable("id") Long id){
	    Optional<Post> optionalPost = postService.findByIdPost(id);
	    if (!optionalPost.isPresent()) {
	        return ResponseEntity.notFound().build();
	    }
	    Post p = optionalPost.get();
	    PostMeDto post = PostMeDto.builder()
	                              .id(p.getId())
	                              .text(p.getText())
	                              .createdAt(p.getCreate_at()) 
	                              .mediaUrl(p.getMedias())
	                              .color(p.getColor())
	                              .background(p.getBackground())
	                              .user(p.getUser().getId())
								  .user_fullname(p.getUser().getFullname())
	                              .group(p.getGroup() != null ? p.getGroup().getId() : null)
	                              .build();
	    return ResponseEntity.ok(post);
	}
	@PostMapping(value = "/share")
	public ResponseEntity<?> Post(@RequestBody PostMeDto dto){
		postService.sharePost(dto);
		return ResponseEntity.ok(dto);
	}
	@GetMapping(value = "/countshare/{postid}")
	public int countShare(@PathVariable("postid") Long id){
		return postService.countShareByPostId(id);
	}
	@GetMapping(value = "/user/friend/{id}")
	public ResponseEntity<List<UserDto>> findUserFriend(@PathVariable Long id){
		List<UserDto> result = postService.findUser(id).stream().map((u)->
			UserDto.builder()
				.id(u.getId())
				.username(u.getUsername())
				.gender(u.getGender())
				.fullname(u.getFullname())
				.build()).toList();
		return ResponseEntity.ok(result);
	}
}
