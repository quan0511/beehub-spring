package vn.aptech.beehub.services.impl;

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

import vn.aptech.beehub.dto.GalleryDto;
import vn.aptech.beehub.dto.GroupDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.ProfileDto;
import vn.aptech.beehub.dto.SearchingDto;
import vn.aptech.beehub.dto.UserDto;
import vn.aptech.beehub.dto.UserSettingDto;
import vn.aptech.beehub.models.EGroupRole;
import vn.aptech.beehub.models.ERelationshipType;
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
	private GroupMemberRepository groupMember;
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
					user.getGroup_joined().size(),
					findAllFriends(user.getId()).size()
					));
		});
		userRep.findRelationship(id, ERelationshipType.BLOCKED.toString()).forEach((user)->{
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
			user.setGroup_counter(t.getGroup_joined().size());
			user.setFriend_counter(0);
			user.setFriend_counter(findAllFriends(id).size());
			return user;
		});
	}
	@Override
	public List<UserDto> findAllFriends(Long id) {
		List<UserDto> list = new LinkedList<UserDto>();
		userRep.findRelationship(id,ERelationshipType.FRIEND.toString()).forEach(e-> list.add(toDto(e)));
		return list;
	}
	@Override
	public Map<String, List<Object>> getGroupJoinedAndFriends(Long id){
		Map<String, List<Object>> res= new HashMap<String, List<Object>>(); 
		List<Object> listGroup =  groupSer.getGroupUserJoined(id);
		List<Object> listFriend = new LinkedList<Object>();
		userRep.findRelationship(id,ERelationshipType.FRIEND.toString()).forEach(e-> listFriend.add(toDto(e)));
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
					relationship = userRe.isPresent()? (userRe.get().getUser1().getId()== id
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
						relationship, user.getGroup_joined().size(),
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
						ERelationshipType.FRIEND.toString(), user.getGroup_joined().size(),
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
				String relationship = relationshipRep.getRelationship(id, user.getId()).isPresent()?relationshipRep.getRelationship(id, user.getId()).get().getType().toString():null;
				listSendRequest.add(new UserDto(
						user.getId(), 
						user.getUsername(), 
						user.getFullname(), 
						user.getGender(),
						user.getImage()!=null?user.getImage().getMedia():null, 
						user.getImage()!=null?user.getImage().getMedia_type():null, 
						relationship, user.getGroup_joined().size(),
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
				relationship = userRe.isPresent()? (userRe.get().getUser1().getId()== user_id
													? userRe.get().getType().toString()
													: "BE_BLOCKED")
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
			List<GalleryDto> galleries = new LinkedList<GalleryDto>();
			galleryRep.findByUser_id(user.getId()).forEach((gallery)->{
				galleries.add(new GalleryDto(
						gallery.getId(), 
						gallery.getUser().getId(),
						gallery.getPost().getId(), 
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
							user.getGroup_joined().size(),
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
	public Optional<UserDto> getUserByEmail(String email) {
		
		return userRep.findByEmail(email).map((e)->toDto(e));
	}
	@Override
	public boolean checkGroupMember(Long id_user, Long id_group) {
		Optional<GroupMember> groupMem = groupMember.findMemberInGroupWithUser(id_group,id_user);
		return groupMem.isPresent() && !groupMem.get().getRole().equals(EGroupRole.MEMBER);
	}
	@Override
	public boolean checkUsernameIsExist(String username) {
		
		return userRep.existsByUsername(username);
	}
	@Override
	public void updateUser(Long id,ProfileDto profile) {
		User user = userRep.findById(id).get();
		user.setUsername(profile.getUsername());
		user.setEmail(profile.getEmail());
		user.setFullname(profile.getFullname());
		user.setGender(profile.getGender());
		user.setPhone(profile.getPhone());
		user.setBirthday(profile.getBirthday());
		userRep.save(user);
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
	public void updatePassword(Long id, String password) {
		User user = userRep.findById(id).get();
		PasswordEncoder passwordEncode = new BCryptPasswordEncoder();
		user.setPassword(passwordEncode.encode(password));
		userRep.save(user);
	}
	
}
