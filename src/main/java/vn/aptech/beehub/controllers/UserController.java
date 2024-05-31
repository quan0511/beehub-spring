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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import vn.aptech.beehub.dto.FileInfo;
import vn.aptech.beehub.dto.GroupDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.ProfileDto;
import vn.aptech.beehub.dto.RequirementDto;
import vn.aptech.beehub.dto.SearchingDto;
import vn.aptech.beehub.dto.UserDto;
import vn.aptech.beehub.dto.UserSettingDto;
import vn.aptech.beehub.services.IFilesStorageService;
import vn.aptech.beehub.services.IGroupService;
import vn.aptech.beehub.services.IPostService;
import vn.aptech.beehub.services.IRequirementService;
import vn.aptech.beehub.services.IUserService;
import vn.aptech.beehub.services.IUserSettingService;

import org.springframework.http.HttpStatus;

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
	
	@GetMapping(path = "/users")
	private List<UserDto> getAllUsers(){
		return userService.findAll();
	}
	@GetMapping(path = "/user/{id}")
	private Optional<UserDto> getUser(@PathVariable Long id){
		return userService.getUser(id);
	}
	@GetMapping(path="/user/{id}/profile/{username}")
	private Optional<ProfileDto> getProfile(@PathVariable Long id,@PathVariable String username){
		return userService.getProfile(username,id);
	}
	@GetMapping(path="/friends/{id}")
	private List<UserDto> getFriends(@PathVariable Long id){
		return userService.findAllFriends(id);
	}
	@GetMapping(path="/groups_friends/{id}")
	private Map<String, List<Object>> getGroupsAndFriends(@PathVariable Long id){
		return userService.getGroupJoinedAndFriends(id);
	}
	@GetMapping(path = "/post/{id}")
	private List<PostDto> getPosts(@PathVariable Long id){
		return postService.findByUserId(id);
	}
	@GetMapping(path = "/homepage/{id}")
	private List<PostDto> getFriendPost(@PathVariable Long id,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "5") int limit){
		return postService.newestPostsForUser(id, page,limit);
	}
	@GetMapping(path = "/load-posts/{id}")
	private List<PostDto> allAllPost(@PathVariable Long id,@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "5") int limit){
		return postService.getPostsForUser(id,page, limit);
	}
	@GetMapping(path = "/allposts/{id}")
	private List<PostDto> allAllPost(@PathVariable Long id){
		return postService.getAllPostForUser(id);
	}
	@GetMapping(path = "/peoplepage/{id}")
	private Map<String, List<UserDto>> getPeople(@PathVariable Long id ){
		return userService.getPeople(id);
	}
	@GetMapping(path = "/listgroup_page/{id}")
	private Map<String, List<GroupDto>> getListGroups(@PathVariable Long id){
		return groupService.getListGroup(id);
	}
	@GetMapping(path = "/user/{id}/search_all")
	private SearchingDto getSearchString(@PathVariable Long id,@RequestParam(required = true) String search){
		return userService.getSearch(id,search);
	}
	@GetMapping (path="user/{id_user}/get-group/{id_group}")
	private GroupDto getGroup(@PathVariable Long id_user, @PathVariable Long id_group) {
		return groupService.getGroup(id_user, id_group);
	}
	@GetMapping (path="/user/{id_user}/group/{id_group}/posts")
	private List<PostDto> getPostInGroup(@PathVariable Long id_user, @PathVariable Long id_group){
		return postService.newestPostInGroup(id_group, id_user, 8);
	}
	@GetMapping(path = "/check-user")
	private boolean checExistUsername (@RequestParam(required = false) String username) {
		if(username!=null && !username.isEmpty()) {
			return userService.checkUsernameIsExist(username);			
		}
		return false;
	}
	@GetMapping(path = "/check-email")
	private boolean checkExistEmail (@RequestParam(required = false) String email) {
		if(email!=null && !email.isEmpty()){
			return userService.getUserByEmail(email).isPresent();
		}
		return false;
	}
	@PostMapping(path = "/update/profile/{id}")
	private void updateUser(@PathVariable("id") Long id, @RequestBody ProfileDto profile) {
		userService.updateUser(id,profile);
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
}
