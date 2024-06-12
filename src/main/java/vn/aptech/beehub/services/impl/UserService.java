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
import vn.aptech.beehub.dto.ReportFormDto;
import vn.aptech.beehub.dto.ReportTypesDto;
import vn.aptech.beehub.dto.RequirementDto;
import vn.aptech.beehub.dto.SearchingDto;
import vn.aptech.beehub.dto.UserDto;
import vn.aptech.beehub.dto.UserSettingDto;
import vn.aptech.beehub.models.EGroupRole;
import vn.aptech.beehub.models.ERelationshipType;
import vn.aptech.beehub.models.Gallery;
import vn.aptech.beehub.models.Group;
import vn.aptech.beehub.models.GroupMember;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.RelationshipUsers;
import vn.aptech.beehub.models.Report;
import vn.aptech.beehub.models.Requirement;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.repository.GalleryRepository;
import vn.aptech.beehub.repository.GroupMemberRepository;
import vn.aptech.beehub.repository.GroupRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.RelationshipUsersRepository;
import vn.aptech.beehub.repository.ReportRepository;
import vn.aptech.beehub.repository.ReportTypeRepository;
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
	private ReportTypeRepository reportTypeRep;
	@Autowired 
	private PostRepository postRep;
	@Autowired
	private GroupRepository groupRep;
	@Autowired
	private ReportRepository reportRep;
	@Autowired
	private S3Service s3Service;
	@Autowired 
	private ModelMapper mapper;
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
					user.getImage()!=null?user.getImage().getMedia_type():null,
					user.is_banned()));
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
					user.is_banned(),
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
					ERelationshipType.BLOCKED.toString(),
					user.is_banned()
					));
		});
		return list;
	}
	@Override
	public List<UserDto> getProfileRelationship(Long id_user,Long id_profile){
		List<UserDto> list = new LinkedList<UserDto>();
		userRep.findProfileRelationship(id_user , id_profile).forEach((user)->{
			list.add(new UserDto(
					user.getId(),
					user.getUsername(), 
					user.getFullname(), 
					user.getGender(), 
					user.getImage()!=null?user.getImage().getMedia():null,
					user.getImage()!=null?user.getImage().getMedia_type():null,
					ERelationshipType.FRIEND.toString(),
					user.is_banned(),
					groupMemberRep.findByUser_id(user.getId()).size(),
					findAllFriends(user.getId()).size()
					));
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
					t.getImage()!=null?t.getImage().getMedia_type():null,
					t.is_banned());
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
				new UserDto(e.getId(), e.getUsername(), e.getFullname(), e.getGender(), e.getImage()!=null?e.getImage().getMedia():null, e.getImage()!=null?e.getImage().getMedia_type():null,e.is_banned())
				));
		return list;
	}
	@Override
	public Map<String, List<Object>> getGroupJoinedAndFriends(Long id){
		Map<String, List<Object>> res= new HashMap<String, List<Object>>(); 
		List<Object> listGroup =  groupSer.getGroupUserJoined(id);
		List<Object> listFriend = new LinkedList<Object>();
		userRep.findRelationship(id,ERelationshipType.FRIEND.toString()).forEach(e-> listFriend.add(
				new UserDto(e.getId(), e.getUsername(), e.getFullname(), e.getGender(), e.getImage()!=null?e.getImage().getMedia():null, e.getImage()!=null?e.getImage().getMedia_type():null,e.is_banned())
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
						Optional<Requirement> requires = requirementRep.getRequirementsBtwUsersIsNotAccept(id, user.getId());
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
						user.is_banned(),
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
						user.is_banned(),
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
						user.is_banned(),
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
			if(!user.is_active()) {
				return null;
			}
			String relationship = null;
			if(user.getId()!=user_id) {
				Optional<RelationshipUsers> userRe= relationshipRep.getRelationship(user_id, user.getId());
				relationship = userRe.isPresent()? (userRe.get().getType().equals(ERelationshipType.FRIEND) || 
													userRe.get().getUser1().getId()== user_id
													? userRe.get().getType().toString()
													:"BE_BLOCKED")
												:null;
				if(userRe.isEmpty()) {
					Optional<Requirement> requires = requirementRep.getRequirementsBtwUsersIsNotAccept(user_id, user.getId());
					relationship = requires.isPresent()? (requires.get().getSender().getId()==user_id?
															"SENT_REQUEST": "NOT_ACCEPT"
														): null;
				}
			}
			List<Object> grList = groupSer.getGroupUserJoined(user.getId());
			List<UserSettingDto> userSetting = userSettingSer.allSettingItemOfUser(user.getId());
			List<UserDto> relationshipList = getProfileRelationship(user_id,user.getId());
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
					user.getPhone(),
					user.is_active(),
					relationship,
					user.getCreate_at(),
					user.is_banned(),
					grList,
					userSetting,
					relationshipList,
					posts,
					galleries,
					user.getCreate_at()
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
						Optional<Requirement> requires = requirementRep.getRequirementsBtwUsersIsNotAccept(id, user.getId());
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
							user.is_banned(),
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
	@Override
	public List<ReportTypesDto> getListReportType() {
		List<ReportTypesDto> list = reportTypeRep.findAll().stream().map((type)->
		ReportTypesDto.builder()
		.id(type.getId())
		.title(type.getTitle())
		.description(type.getDescription())
		.build()).toList();
		return list;
	}
	@Override
	public String createReport(Long id_user, ReportFormDto report) {
		Optional<User> findUser = userRep.findById(id_user);
		if(findUser.isPresent()) 	{
			Report createreport = new Report();
			if(report.getTarget_user_id()!=null) {
				userRep.findById(report.getTarget_user_id()).ifPresent((user)-> {
					createreport.setTarget_user(user);				
				});				
			}
			if(report.getTarget_post_id()!=null) {
				postRep.findById(report.getTarget_post_id()).ifPresent((post)->{
					createreport.setTarget_post(post);
				});				
			}
			if(report.getTarget_group_id()!=null) {
				groupRep.findById(report.getTarget_group_id()).ifPresent((group)->{
					createreport.setTarget_group(group);
				});				
			}
			createreport.setSender(findUser.get());
			createreport.setAdd_description(report.getAdd_description());
			createreport.setCreate_at(LocalDateTime.now());
			createreport.setUpdate_at(LocalDateTime.now());
			createreport.setReport_type(reportTypeRep.findById(report.getType_id()).get());
			
			reportRep.save(createreport);
			return "success";
		}
		return "unsuccess";
	}
	@Override
	public String getUsername(Long id) {
		Optional<User> findUser = userRep.findById(id);
		if(findUser.isPresent()) {
			return findUser.get().getUsername();
		}
		return null;
	}
	@Override
	public List<RequirementDto> getNotification(Long id) {
		List<Requirement> getReq = requirementRep.getNotification(id);
		List<RequirementDto> getRequirement = new LinkedList<RequirementDto>();
		getReq.forEach((req)-> {
			RequirementDto require = new RequirementDto();
			require.setId(req.getId());
			UserDto sender = new UserDto(req.getSender().getId(), req.getSender().getUsername(), req.getSender().getFullname(), req.getSender().getGender(), req.getSender().getImage()!=null?req.getSender().getImage().getMedia():null, req.getSender().getImage()!=null? req.getSender().getImage().getMedia_type():null, req.getSender().is_banned());
			if(req.getReceiver()!=null) {
				UserDto receiver= new UserDto(req.getReceiver().getId(), req.getReceiver().getUsername(), req.getReceiver().getFullname(), req.getReceiver().getGender(), req.getReceiver().getImage()!=null?req.getReceiver().getImage().getMedia():null, req.getReceiver().getImage()!=null? req.getReceiver().getImage().getMedia_type():null, req.getReceiver().is_banned());
				require.setReceiver(receiver);				
			}
			require.setSender_id(req.getSender().getId());
			require.setSender(sender);
			if(req.getGroup_receiver()!=null) {
				GroupDto group = new GroupDto(req.getGroup_receiver().getId(), req.getGroup_receiver().getGroupname(), req.getGroup_receiver().isActive(), req.getGroup_receiver().getImage_group()!=null? req.getGroup_receiver().getImage_group().getMedia():null);				
				require.setGroup(group);
			}
			require.set_accept(req.is_accept());
			require.setCreate_at(req.getCreate_at());
			require.setReceiver_id(req.getReceiver()!=null?req.getReceiver().getId():null);
			require.setType(req.getType().toString());
			getRequirement.add(require);
		});
		return getRequirement;
	}
	
	
}
