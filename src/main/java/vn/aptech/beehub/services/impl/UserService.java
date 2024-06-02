package vn.aptech.beehub.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.aptech.beehub.aws.S3Service;
import vn.aptech.beehub.dto.GalleryDto;
import vn.aptech.beehub.dto.GroupDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.ProfileDto;
import vn.aptech.beehub.dto.SearchingDto;
import vn.aptech.beehub.dto.UserDto;
import vn.aptech.beehub.dto.UserSettingDto;
import vn.aptech.beehub.models.EGroupRole;
import vn.aptech.beehub.models.ERelationshipType;
import vn.aptech.beehub.models.Gallery;
import vn.aptech.beehub.models.GroupMember;
import vn.aptech.beehub.models.RelationshipUsers;
import vn.aptech.beehub.models.Requirement;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.repository.GalleryRepository;
import vn.aptech.beehub.repository.GroupMemberRepository;
import vn.aptech.beehub.repository.RelationshipUsersRepository;
import vn.aptech.beehub.repository.RequirementRepository;
import vn.aptech.beehub.repository.UserRepository;
import vn.aptech.beehub.services.IGroupService;
import vn.aptech.beehub.services.IPostService;
import vn.aptech.beehub.services.IUserService;
import vn.aptech.beehub.services.IUserSettingService;

@Service
public class UserService implements IUserService {
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private UserRepository userRep;
	@Autowired
	private GalleryRepository galleryRep;
	@Autowired
	private RelationshipUsersRepository relationshipRep;
	@Autowired
	private RequirementRepository requirementRep;
	@Autowired 
	private IPostService postSer;
	@Autowired
	private IUserSettingService userSettingSer;
	@Autowired
	private IGroupService groupSer;
	@Autowired
	private GroupMemberRepository groupMemberRep;
	@Autowired
	private S3Service s3Service;
	@Autowired 
	private ModelMapper mapper;
	private UserDto toDto(User user) {
		return mapper.map(user, UserDto.class);
	}
	@Override
	public List<UserDto> findAll() {
		List<UserDto> list = new LinkedList<UserDto>();
		userRep.findAll().forEach((user)-> {
			list.add(new UserDto(
					user.getId(), 
					user.getUsername(),
					user.getFullname(),
					user.getGender(),
					user.getImage()!=null? user.getImage().getMedia():null,
					user.getImage()!=null?user.getImage().getMedia_type():null));
		});
		return list;
	}
	@Override
	public List<UserDto> getRelationship(Long id){
		List<UserDto> list = new LinkedList<UserDto>();
		userRep.findRelationship(id, ERelationshipType.FRIEND.toString()).forEach((user)->{
			list.add(new UserDto(
					user.getId(),
					user.getUsername(), 
					user.getFullname(), 
					user.getGender(), 
					user.getImage()!=null?user.getImage().getMedia():null,
					user.getImage()!=null?user.getImage().getMedia_type():null,
					ERelationshipType.FRIEND.toString(),
					groupMemberRep.findByUser_id(user.getId()).size(),
					findAllFriends(user.getId()).size()
					));
		});
		userRep.findBlocked(id).forEach((user)->{
			list.add(new UserDto(
					user.getId(),
					user.getUsername(), 
					user.getFullname(), 
					user.getGender(), 
					user.getImage()!=null?user.getImage().getMedia():null, 
					user.getImage()!=null?user.getImage().getMedia_type():null,		
					ERelationshipType.BLOCKED.toString()));
		});
		return list;
	}
	@Override
	public Optional<UserDto> getUser(Long id) {
		return userRep.findById(id).map(t -> {
			UserDto user = new UserDto(
					t.getId(), 
					t.getUsername(), 
					t.getFullname(), 
					t.getGender(), 
					t.getImage()!=null?t.getImage().getMedia():null,
					t.getImage()!=null?t.getImage().getMedia_type():null);
			user.setGroup_counter(groupMemberRep.findByUser_id(id).size());
			user.setFriend_counter(0);
			user.setFriend_counter(findAllFriends(id).size());
			return user;
		});
	}
	@Override
	public List<UserDto> findAllFriends(Long id) {
		List<UserDto> list = new LinkedList<UserDto>();
		userRep.findRelationship(id,ERelationshipType.FRIEND.toString()).forEach(e-> list.add(
				new UserDto(e.getId(), e.getUsername(), e.getFullname(), e.getGender(), e.getImage()!=null?e.getImage().getMedia():null, e.getImage()!=null?e.getImage().getMedia_type():null)
				));
		return list;
	}
	@Override
	public Map<String, List<Object>> getGroupJoinedAndFriends(Long id){
		Map<String, List<Object>> res= new HashMap<String, List<Object>>(); 
		List<Object> listGroup =  groupSer.getGroupUserJoined(id);
		List<Object> listFriend = new LinkedList<Object>();
		userRep.findRelationship(id,ERelationshipType.FRIEND.toString()).forEach(e-> listFriend.add(
				new UserDto(e.getId(), e.getUsername(), e.getFullname(), e.getGender(), e.getImage()!=null?e.getImage().getMedia():null, e.getImage()!=null?e.getImage().getMedia_type():null)
				));
		res.put("groups", listGroup);
		res.put("friends", listFriend);
		return res;
	}
	@Override
	public Map<String, List<UserDto>> getPeople(Long id) {
		Map<String, List<UserDto>> getMap = new HashMap<String, List<UserDto>>();
		List<UserDto> listPeople = new LinkedList<UserDto>();
		try {
			userRep.findPeopleSameGroup(id).forEach((user)-> {
				String relationship = null;
				if(user.getId()!=id) {
					Optional<RelationshipUsers> userRe= relationshipRep.getRelationship(id, user.getId());
					relationship = userRe.isPresent()? (userRe.get().getUser1().getId()== id || userRe.get().getType().equals(ERelationshipType.FRIEND) 
														? userRe.get().getType().toString()
														: "BE_BLOCKED")
													:null;
					if(userRe.isEmpty()) {
						Optional<Requirement> requires = requirementRep.getRequirementsBtwUsers(id, user.getId());
						relationship = requires.isPresent()? (requires.get().getSender().getId()==id?
																"SENT_REQUEST": "NOT_ACCEPT"
															): null;
					}
				}
				listPeople.add(new UserDto(
						user.getId(), 
						user.getUsername(), 
						user.getFullname(), 
						user.getGender(),
						user.getImage()!=null?user.getImage().getMedia():null, 
						user.getImage()!=null?user.getImage().getMedia_type():null, 
						relationship, 
						groupMemberRep.findByUser_id(user.getId()).size(),
						findAllFriends(user.getId()).size())
						);
				});
		} catch (Exception e2) {
			logger.error(e2.getMessage());
		} finally {
			getMap.put("people", listPeople);
		}

		List<UserDto> listFriends = new LinkedList<UserDto>();
		try {
			userRep.findRelationship(id,ERelationshipType.FRIEND.toString()).forEach((user)-> {
				listFriends.add(new UserDto(
						user.getId(), 
						user.getUsername(), 
						user.getFullname(), 
						user.getGender(),
						user.getImage()!=null?user.getImage().getMedia():null, 
						user.getImage()!=null?user.getImage().getMedia_type():null, 
						ERelationshipType.FRIEND.toString(), 
						groupMemberRep.findByUser_id(user.getId()).size(),
						findAllFriends(user.getId()).size())
						);
			});
		} catch (Exception e2) {
			logger.error(e2.getMessage());
		} finally {
			getMap.put("friends", listFriends);			
		}
		
		List<UserDto> listSendRequest = new LinkedList<UserDto>();
		try {
			userRep.findPeopleUserSendAddFriend(id).forEach((user)->{
				listSendRequest.add(new UserDto(
						user.getId(), 
						user.getUsername(), 
						user.getFullname(), 
						user.getGender(),
						user.getImage()!=null?user.getImage().getMedia():null, 
						user.getImage()!=null?user.getImage().getMedia_type():null, 
						"SENT_REQUEST", 
						groupMemberRep.findByUser_id(user.getId()).size(),
						findAllFriends(user.getId()).size())
						);
			});			
		} catch (Exception e2) {
			logger.error(e2.getMessage());
		}finally {
			
			getMap.put("addfriend", listSendRequest);
		}
		return getMap;
	}
	
	@Override
	public Optional<ProfileDto> getProfile(String username, Long user_id) {
		return userRep.findByUsername(username).map((user)-> {
			String relationship = null;
			if(user.getId()!=user_id) {
				Optional<RelationshipUsers> userRe= relationshipRep.getRelationship(user_id, user.getId());
				relationship = userRe.isPresent()? (userRe.get().getType().equals(ERelationshipType.FRIEND) || 
													userRe.get().getUser1().getId()== user_id
													? userRe.get().getType().toString()
													:"BE_BLOCKED")
												:null;
				if(userRe.isEmpty()) {
					Optional<Requirement> requires = requirementRep.getRequirementsBtwUsers(user_id, user.getId());
					relationship = requires.isPresent()? (requires.get().getSender().getId()==user_id?
															"SENT_REQUEST": "NOT_ACCEPT"
														): null;
				}
			}
			List<Object> grList = groupSer.getGroupUserJoined(user.getId());
			List<UserSettingDto> userSetting = userSettingSer.allSettingItemOfUser(user.getId());
			List<UserDto> relationshipList = getRelationship(user.getId());
			List<PostDto> posts = postSer.findByUserId(user.getId());
			if(relationship != "FRIEND" && user_id != user.getId() ) {
				posts.removeIf((post)-> post.getSetting_type()=="HIDDEN" || post.getSetting_type() =="FOR_FRIEND");
			}else if(relationship == "FRIEND" && user_id != user.getId()  ){
				posts.removeIf((post)-> post.getSetting_type()=="HIDDEN");
			}
			List<GalleryDto> galleries = new LinkedList<GalleryDto>();
			galleryRep.findByUser_id(user.getId()).forEach((gallery)->{
				galleries.add(new GalleryDto(
						gallery.getId(), 
						gallery.getUser().getId(),
						gallery.getPost()!=null?gallery.getPost().getId():null, 
						gallery.getMedia(), 
						gallery.getMedia_type(), 
						gallery.getCreate_at()));
			});
			return new ProfileDto(
					user.getId(),
					user.getUsername(),
					user.getEmail(),
					user.getFullname(),
					user.getGender(),
					user.getImage()!=null?user.getImage().getMedia():null,
					user.getBackground()!=null?user.getBackground().getMedia():null,
					user.getBio(),
					user.getBirthday(),
					user.isEmail_verified(),
					user.getPhone(),
					user.is_active(),
					relationship,
					user.getCreate_at(),
					grList,
					userSetting,
					relationshipList,
					posts,
					galleries
					);
		});
		
	}
	@Override
	public SearchingDto getSearch(Long id,String search) {
		//Search posts
		List<PostDto> listPosts = postSer.getSearchPosts(search, id);
		//Search pepple
		List<UserDto> listPeople = new LinkedList<UserDto>();
		userRep.searchPeople(search,id).forEach((user)->{
			try {
				Optional<RelationshipUsers> getRelationship =relationshipRep.getRelationship(id, user.getId());
				if(getRelationship.isEmpty()|| !(getRelationship.get().getUser2().getId()==id &&getRelationship.get().getType().equals(ERelationshipType.BLOCKED))) {
					String relationship = getRelationship.isPresent()? getRelationship.get().getType().toString():null;
					if(relationship==null) {
						Optional<Requirement> requires = requirementRep.getRequirementsBtwUsers(id, user.getId());
						relationship = requires.isPresent()? (requires.get().getSender().getId()==id?
								"SENT_REQUEST": "NOT_ACCEPT"
								): null;						
					}
					listPeople.add(new UserDto(
							user.getId(),
							user.getUsername(), 
							user.getFullname(), 
							user.getGender(), 
							user.getImage()!=null?user.getImage().getMedia():null,
							user.getImage()!=null?user.getImage().getMedia_type():null,
							relationship,
							groupMemberRep.findByUser_id(user.getId()).size(),
							findAllFriends(user.getId()).size()));									
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		});
		//Search groups
		List<GroupDto> listGroups = groupSer.searchNameGroup(search, id);
		SearchingDto searchDto = new SearchingDto();
		searchDto.setPosts(listPosts);
		searchDto.setPeople(listPeople);
		searchDto.setGroups(listGroups);
		return searchDto;
	}
	@Override
	public Optional<User> getUserByEmail(String email) {
			return userRep.findByEmail(email);
	}
	@Override
	public boolean checkGroupMember(Long id_user, Long id_group) {
		Optional<GroupMember> groupMem = groupMemberRep.findMemberInGroupWithUser(id_group,id_user);
		return groupMem.isPresent() && !groupMem.get().getRole().equals(EGroupRole.MEMBER);
	}
	@Override
	public boolean checkUsernameIsExist(String username) {
		return userRep.existsByUsername(username);
	}
	@Override
	public boolean updateUser(Long id,ProfileDto profile) {
		try {
			User user = userRep.findById(id).get();
			user.setUsername(profile.getUsername());
			user.setEmail(profile.getEmail());
			user.setFullname(profile.getFullname());
			user.setGender(profile.getGender());
			user.setPhone(profile.getPhone());
			user.setBirthday(profile.getBirthday());
			userRep.save(user);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	public boolean updateBio(Long id, ProfileDto user) {
		try {
			User userUp = userRep.findById(id).get();
			userUp.setBio(user.getBio());
			userRep.save(userUp);			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	public boolean checkPassword(Long id, String password) {
		Optional<User> user = userRep.findById(id);
		if(user.isPresent()) {
			PasswordEncoder passwordEncode = new BCryptPasswordEncoder();
			return passwordEncode.matches(password, user.get().getPassword());
		}
		return false;
	}
	@Override
	public boolean updatePassword(Long id, String password) {
		try {
			User user = userRep.findById(id).get();
			PasswordEncoder passwordEncode = new BCryptPasswordEncoder();
			user.setPassword(passwordEncode.encode(password));
			userRep.save(user);			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Transactional
	@Override
	public boolean updateImage(Long id, String image) {
		Optional<User> findUser = userRep.findById(id);
		if(findUser.isPresent()&& image!=null && !image.isEmpty()) {
			User user = findUser.get();
			try {
//				if(user.getImage()!=null) {
//					String fileUrl = user.getImage().getMedia();
//					String fileExtract= fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//					s3Service.deleteToS3(fileExtract);
//					Gallery gall= user.getImage();
//					galleryRep.deleteGallery(gall.getId());
//					logger.info("Deleted Old image");
//				}
				Gallery newGallery = galleryRep.save(new Gallery(user, image, "image", LocalDateTime.now()));
				user.setImage(newGallery);
				userRep.save(user);
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return false;
	}
	@Transactional
	@Override
	public boolean updateBackground(Long id, String background) {
		Optional<User> findUser = userRep.findById(id);
		if(findUser.isPresent()&& background!=null && !background.isEmpty()) {
			User user = findUser.get();
			try {
//				if(user.getBackground()!=null) {
//					String fileUrl = user.getBackground().getMedia();
//					String fileExtract= fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//					s3Service.deleteToS3(fileExtract);
//					Gallery gall= user.getBackground();
//					galleryRep.deleteGallery(gall.getId());
//					logger.info("Deleted Old background");
//				}
				Gallery newGallery = galleryRep.save(new Gallery(user, background, "image", LocalDateTime.now()));
				user.setBackground(newGallery);
				userRep.save(user);
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return false;
	}
	
	
}
