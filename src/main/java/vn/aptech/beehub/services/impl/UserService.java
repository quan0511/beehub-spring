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
import org.springframework.stereotype.Service;

import vn.aptech.beehub.dto.GalleryDto;
import vn.aptech.beehub.dto.GroupDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.dto.ProfileDto;
import vn.aptech.beehub.dto.SearchingDto;
import vn.aptech.beehub.dto.UserDto;
import vn.aptech.beehub.dto.UserSettingDto;
import vn.aptech.beehub.models.ERelationshipType;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.repository.GalleryRepository;
import vn.aptech.beehub.repository.RelationshipUsersRepository;
import vn.aptech.beehub.repository.UserRepository;
import vn.aptech.beehub.seeders.DatabaseSeeder;
import vn.aptech.beehub.services.IGroupService;
import vn.aptech.beehub.services.IPostService;
import vn.aptech.beehub.services.IUserService;
import vn.aptech.beehub.services.IUserSettingService;


@Service
public class UserService implements IUserService {
	private Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
	@Autowired
	private UserRepository userRep;
	@Autowired
	private GalleryRepository galleryRep;
	@Autowired
	private RelationshipUsersRepository relationshipRep;
	@Autowired 
	private IPostService postSer;
	@Autowired
	private IUserSettingService userSettingSer;
	@Autowired
	private IGroupService groupSer;
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
				String relationship = relationshipRep.getRelationship(id, user.getId()).isPresent()?relationshipRep.getRelationship(id, user.getId()).get().getType().toString():null;
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
				String relationship = relationshipRep.getRelationship(id, user.getId()).isPresent()?relationshipRep.getRelationship(id, user.getId()).get().getType().toString():null;
				listFriends.add(new UserDto(
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
	public Optional<ProfileDto> getProfile(String username) {
		return userRep.findByUsername(username).map((user)-> {
			List<Object> grList = groupSer.getGroupUserJoined(user.getId());
			List<UserSettingDto> userSetting = userSettingSer.allSettingOfUser(user.getId());
			List<UserDto> relationshipList = getRelationship(user.getId());
			List<PostDto> posts = postSer.findByUserId(user.getId());
			List<GalleryDto> galleries = new LinkedList<GalleryDto>();
			galleryRep.findByUser_id(user.getId()).forEach((gallery)->{
				galleries.add(new GalleryDto(gallery.getId(), gallery.getUser().getId(),gallery.getPost().getId(), gallery.getMedia(), gallery.getMedia_type(), gallery.getCreate_at()));
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
					user.getActive_at(),
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
				String relationship = relationshipRep.getRelationship(id, user.getId()).isPresent()?relationshipRep.getRelationship(id, user.getId()).get().getType().toString():null;
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
	
	
	
}
