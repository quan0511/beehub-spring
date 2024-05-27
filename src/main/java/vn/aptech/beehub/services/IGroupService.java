package vn.aptech.beehub.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import vn.aptech.beehub.dto.GroupDto;


public interface IGroupService {
	public List<GroupDto> searchNameGroup(String search, Long id_user);
	public Map<String, List<GroupDto>> getListGroup(Long id);
	public Optional<GroupDto> getGroup(Long id_user, Long id_group);
	public List<Object> getGroupUserJoined (Long id);
}
