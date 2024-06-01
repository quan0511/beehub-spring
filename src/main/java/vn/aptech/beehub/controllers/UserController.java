package vn.aptech.beehub.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import vn.aptech.beehub.aws.S3Service;
import vn.aptech.beehub.dto.FileInfo;
import vn.aptech.beehub.dto.GroupDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.PostMeDto;
import vn.aptech.beehub.dto.ProfileDto;
import vn.aptech.beehub.dto.RequirementDto;
import vn.aptech.beehub.dto.SearchingDto;
import vn.aptech.beehub.dto.UserDto;
import vn.aptech.beehub.dto.UserSettingDto;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.services.IFilesStorageService;
import vn.aptech.beehub.services.IGroupService;
import vn.aptech.beehub.services.IPostService;
import vn.aptech.beehub.services.IRequirementService;
import vn.aptech.beehub.services.IUserService;
import vn.aptech.beehub.services.IUserSettingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Tag(name = "User")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired 
	private IUserService userService;
	@Autowired
	private IPostService postService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IFilesStorageService storageService;
	@Autowired
	private IUserSettingService userSettingService;
	@Autowired
	private IRequirementService requirementService;
	@Autowired
	private S3Service s3Service;
	@GetMapping(path = "/users")
	private ResponseEntity<List<UserDto>> getAllUsers(){
		return ResponseEntity.ok(userService.findAll());
	}
	@GetMapping(path = "/user/{id}")
	private ResponseEntity<Optional<UserDto>> getUser(@PathVariable Long id){
		return ResponseEntity.ok(userService.getUser(id));
	}
	@GetMapping(path="/user/{id}/profile/{username}")
	private ResponseEntity<Optional<ProfileDto>> getProfile(@PathVariable Long id,@PathVariable String username){
		return ResponseEntity.ok(userService.getProfile(username,id));
	}
	@GetMapping(path="/friends/{id}")
	private ResponseEntity<List<UserDto>> getFriends(@PathVariable Long id){
		return ResponseEntity.ok(userService.findAllFriends(id));
	}
	@GetMapping(path="/groups_friends/{id}")
	private ResponseEntity<Map<String, List<Object>>> getGroupsAndFriends(@PathVariable Long id){
		return ResponseEntity.ok(userService.getGroupJoinedAndFriends(id));
	}
	@GetMapping(path = "/post/{id}")
	private ResponseEntity<List<PostDto>> getPosts(@PathVariable Long id){
		return ResponseEntity.ok(postService.findByUserId(id));
	}
	@GetMapping(path = "/homepage/{id}")
	private ResponseEntity<List<PostDto>> getFriendPost(@PathVariable Long id,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "5") int limit){
		return ResponseEntity.ok(postService.newestPostsForUser(id, page,limit));
	}
	@GetMapping(path = "/load-posts/{id}")
	private ResponseEntity<List<PostDto>> allAllPost(@PathVariable Long id,@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "5") int limit){
		return ResponseEntity.ok(postService.getPostsForUser(id,page, limit));
	}
	@GetMapping(path = "/allposts/{id}")
	private ResponseEntity<List<PostDto>> allAllPost(@PathVariable Long id){
		return ResponseEntity.ok(postService.getAllPostForUser(id));
	}
	@GetMapping(path = "/peoplepage/{id}")
	private ResponseEntity<Map<String, List<UserDto>>> getPeople(@PathVariable Long id ){
		return ResponseEntity.ok(userService.getPeople(id));
	}
	@GetMapping(path = "/listgroup_page/{id}")
	private ResponseEntity<Map<String, List<GroupDto>>> getListGroups(@PathVariable Long id){
		return ResponseEntity.ok(groupService.getListGroup(id));
	}
	@GetMapping(path = "/user/{id}/search_all")
	private ResponseEntity<SearchingDto> getSearchString(@PathVariable Long id,@RequestParam(required = true) String search){
		return ResponseEntity.ok(userService.getSearch(id,search));
	}
	@GetMapping (path="/user/{id_user}/get-group/{id_group}")
	private ResponseEntity<GroupDto> getGroup(@PathVariable Long id_user, @PathVariable Long id_group) {
		return ResponseEntity.ok(groupService.getGroup(id_user, id_group));
	}
	@GetMapping (path="/user/{id_user}/group/{id_group}/posts")
	private ResponseEntity<List<PostDto>> getPostInGroup(@PathVariable Long id_user, @PathVariable Long id_group){
		return ResponseEntity.ok(postService.newestPostInGroup(id_group, id_user, 8));
	}
	@GetMapping(path = "/check-user")
	private ResponseEntity<Boolean> checExistUsername (@RequestParam(required = false) String username) {
		if(username!=null && !username.isEmpty()) {
			return ResponseEntity.ok(userService.checkUsernameIsExist(username));			
		}
		return ResponseEntity.ok(false);
	}
	@GetMapping(path = "/check-email")
	private ResponseEntity<Boolean> checkExistEmail (@RequestParam(required = false) String email) {
		if(email!=null && !email.isEmpty()){
			return ResponseEntity.ok(userService.getUserByEmail(email).isPresent());
		}
		return ResponseEntity.ok(false);
	}
	@PostMapping(path = "/update/profile/{id}")
	private void updateUser(@PathVariable("id") Long id, @RequestBody ProfileDto profile) {
		userService.updateUser(id,profile);
	}
	@PostMapping(path = "/update/bio-profile/{id}")
	private void updateBioUser(@PathVariable("id") Long id, @RequestBody ProfileDto profile) {
		userService.updateBio(id,profile);
	}
	@GetMapping(path="/check-password/{id}")
	private boolean checkPassword (@PathVariable("id") Long id,@RequestParam(required = true) String password) {
		logger.info(password);
		return userService.checkPassword(id, password);
	}
	@PostMapping(path = "/update/profile/password/{id}")
	private void updateUserPassword(@PathVariable("id") Long id,@RequestBody  String password) {
		userService.updatePassword(id,password);
	}
	@GetMapping(path = "/check-setting/post/{id}")
	private Map<String, String> checkSettingPost (@PathVariable("id") Long id) {
		return userSettingService.checkSettingPost(id);
	}
	@PostMapping(path = "/update/setting/{id}")
	private Map<String, Integer> updateSettingPost(@PathVariable("id") Long id,@RequestBody String settingType) {
		return userSettingService.settingAllPost(id, settingType);
	}
	@PostMapping(path = "/setting/add/{id}")
	private void updateSettingProfile (@PathVariable("id") Long id,@RequestBody Map<String,String> settingItem) {
		userSettingService.updateSettingItem(id, settingItem);
	}
	@GetMapping(path= "/get-setting/item/{id}")
	private List<UserSettingDto> getAllSettingItem(@PathVariable("id") Long id){
		return userSettingService.allSettingItemOfUser(id);
	}
	@PostMapping(path = "/update/group/{id}")
	private Map<String, Boolean> updateGroup(@PathVariable("id") Long id, @RequestBody GroupDto group){
		return groupService.updateGroup(id, group);
	}
	@PostMapping(path="/send-requirement/{id}")
	private Map<String, String> createRelationship(@PathVariable("id") Long id,@RequestBody RequirementDto requirement){
		return requirementService.handleRequirement(id, requirement);
	}
	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = storageService.load(filename);
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
	 }
	@GetMapping("/files")
	  public ResponseEntity<List<FileInfo>> getListFiles() {
	    List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
	      String filename = path.getFileName().toString();
	      String url = MvcUriComponentsBuilder
	          .fromMethodName(UserController.class, "getFile", path.getFileName().toString()).build().toString();

	      return new FileInfo(filename, url);
	    }).collect(Collectors.toList());

	    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	  }
	@PostMapping(value = "/upload/profile/image/{id}")
	public ResponseEntity<Boolean> uploadImageProfile(@PathVariable("id") Long id,@RequestParam(name= "media",required = true) MultipartFile media) {
	    try {
	        if (media != null && !media.isEmpty()) {
	            String fileUrl = s3Service.uploadToS3(media.getInputStream(), media.getOriginalFilename());
	            
	            boolean result = userService.updateImage(id,fileUrl);
	            return ResponseEntity.ok(result);
	        }else {
	        	logger.error("Not found media");
	        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	    } catch (Exception e) {
	        logger.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	@PostMapping(value = "/upload/profile/background/{id}")
	public ResponseEntity<Boolean> uploadBackgroundProfile(@PathVariable("id") Long id,@RequestParam(name= "media",required = true) MultipartFile media) {
	    try {
	        if (media != null && !media.isEmpty()) {
	            String fileUrl = s3Service.uploadToS3(media.getInputStream(), media.getOriginalFilename());
	            boolean result = userService.updateBackground(id,fileUrl);
	            return ResponseEntity.ok(result);
	        }else {
	        	logger.error("Not found media");
	        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	    } catch (Exception e) {
	        logger.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	@PostMapping(value = "/upload/group/image/{id}")
	public ResponseEntity<Boolean> uploadImageGroup(@PathVariable("id") Long id,@RequestParam(name= "media",required = true) MultipartFile media, @ModelAttribute  GroupDto group) {
	    try {
	        if (media != null && !media.isEmpty()) {
	            String fileUrl = s3Service.uploadToS3(media.getInputStream(), media.getOriginalFilename());
	            group.setImage_group(fileUrl);
	            boolean result = groupService.uploadImage(id,group);
	            return ResponseEntity.ok(result);
	        }else {
	        	logger.error("Not found media");
	        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	    } catch (Exception e) {
	        logger.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	@PostMapping(value = "/upload/group/background/{id}")
	public ResponseEntity<Boolean> uploadBackgroundGroup(@PathVariable("id") Long id,@RequestParam(name= "media",required = true) MultipartFile media, @ModelAttribute  GroupDto group) {
	    try {
	        if (media != null && !media.isEmpty()) {
	            String fileUrl = s3Service.uploadToS3(media.getInputStream(), media.getOriginalFilename());
	            group.setBackground_group(fileUrl);
	            boolean result = groupService.uploadBackground(id,group);
	            return ResponseEntity.ok(result);
	        }else {
	        	logger.error("Not found media");
	        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	    } catch (Exception e) {
	        logger.error(e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
}
