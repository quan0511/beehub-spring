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
	public Optional<ProfileDto> getProfile(String username);
	public List<UserDto> getRelationship(Long id);
	public Map<String, List<Object>> getGroupJoinedAndFriends(Long id);
	public Map<String, List<UserDto>> getPeople(Long id);
	
	public SearchingDto getSearch(Long id,String search);
}
