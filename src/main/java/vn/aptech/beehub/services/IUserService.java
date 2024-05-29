package vn.aptech.beehub.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import vn.aptech.beehub.dto.ProfileDto;
import vn.aptech.beehub.dto.SearchingDto;
import vn.aptech.beehub.dto.UserDto;


public interface IUserService {
	public List<UserDto> findAll();
	public List<UserDto> findAllFriends(Long id);
	public Optional<UserDto> getUser(Long id);
	public Optional<ProfileDto> getProfile(String username,Long id);
	public List<UserDto> getRelationship(Long id);
	public Map<String, List<Object>> getGroupJoinedAndFriends(Long id);
	public Map<String, List<UserDto>> getPeople(Long id);
	public SearchingDto getSearch(Long id,String search);
	public Optional<UserDto> getUserByEmail(String email);
	public boolean checkGroupMember (Long id_user, Long id_group);
	public boolean checkUsernameIsExist(String username);
	public boolean checkPassword(Long id, String password);
	public void updateUser(Long id,ProfileDto user);
	public void updatePassword(Long id, String password);
}
