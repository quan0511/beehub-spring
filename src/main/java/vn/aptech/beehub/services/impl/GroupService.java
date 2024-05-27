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

import vn.aptech.beehub.dto.GroupDto;
import vn.aptech.beehub.dto.GroupMediaDto;
import vn.aptech.beehub.dto.GroupMemberDto;
import vn.aptech.beehub.dto.ReportDto;
import vn.aptech.beehub.dto.RequirementDto;
import vn.aptech.beehub.dto.UserDto;
import vn.aptech.beehub.models.EGroupRole;
import vn.aptech.beehub.models.Group;
import vn.aptech.beehub.models.GroupMember;
import vn.aptech.beehub.models.RelationshipUsers;
import vn.aptech.beehub.repository.GroupMediaRepository;
import vn.aptech.beehub.repository.GroupMemberRepository;
import vn.aptech.beehub.repository.GroupRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.RelationshipUsersRepository;
import vn.aptech.beehub.repository.ReportRepository;
import vn.aptech.beehub.repository.RequirementRepository;
import vn.aptech.beehub.seeders.DatabaseSeeder;
import vn.aptech.beehub.services.IGroupService;


@Service
public class GroupService implements IGroupService {
	private Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
	@Autowired
	private GroupRepository groupRep;
	@Autowired
	private GroupMemberRepository groupMemberRep;
	@Autowired
	private GroupMediaRepository groupMediaRep;
	@Autowired
	private RequirementRepository requireRep;
	@Autowired
	private PostRepository postRep;
	@Autowired
	private ReportRepository reportRep;
	
	@Autowired
	private RelationshipUsersRepository relationshipRep;
	@Autowired 
	private ModelMapper mapper;
	private GroupDto toDto(Group group) {
		return mapper.map(group, GroupDto.class);
	}
	
	@Override
	public List<GroupDto> searchNameGroup(String search, Long id_user) {
		List<GroupDto> listGroups = new LinkedList<GroupDto>();
		groupRep.findByGroupnameContains(search).forEach((group)->{
			try {
				Optional<GroupMember> groupmem = groupMemberRep.findMemberInGroupWithUser(group.getId(), id_user);
				GroupDto groupD = new GroupDto(
						group.getId(), 
						group.getGroupname(), 
						group.isPublic_group(), 
						group.getDescription(), 
						group.isActive(), 
						group.getCreated_at(), 
						group.getImage_group() !=null?group.getImage_group().getMedia():null, 
						group.getBackground_group() !=null?group.getBackground_group().getMedia():null,
						groupmem.isPresent(),
						groupmem.isPresent()? groupmem.get().getRole().toString(): null,
						group.getGroup_members().size()
						);
				listGroups.add(groupD);
				
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		});
		return listGroups;
	}
	@Override
	public Map<String, List<GroupDto>> getListGroup(Long id_user) {
		Map<String, List<GroupDto>> mapGroup = new HashMap<String, List<GroupDto>>();
		List<GroupDto> groupJoined = new LinkedList<GroupDto>();
		groupRep.findAllGroupJoined(id_user).forEach((group)->{
			Optional<GroupMember> getMem = groupMemberRep.findMemberInGroupWithUser(group.getId(), id_user);
			groupJoined.add(new GroupDto(
					group.getId(), 
					group.getGroupname(), 
					group.isPublic_group(), 
					group.getDescription(), 
					group.isActive(), 
					group.getCreated_at(), 
					group.getImage_group() !=null?group.getImage_group().getMedia():null, 
					group.getBackground_group() !=null?group.getBackground_group().getMedia():null,
					getMem.isPresent(),
					getMem.isPresent()?getMem.get().getRole().toString():null,
					group.getGroup_members().size()
					));
		});
		List<GroupDto> groupOwn = new LinkedList<GroupDto>();
		groupRep.findAllOwnGroup(id_user).forEach((group)->{
			Optional<GroupMember> getMem = groupMemberRep.findMemberInGroupWithUser(group.getId(), id_user);
			groupOwn.add(new GroupDto(
					group.getId(), 
					group.getGroupname(), 
					group.isPublic_group(), 
					group.getDescription(), 
					group.isActive(), 
					group.getCreated_at(), 
					group.getImage_group() !=null?group.getImage_group().getMedia():null, 
					group.getBackground_group() !=null?group.getBackground_group().getMedia():null,
					getMem.isPresent(),
					getMem.isPresent()?getMem.get().getRole().toString():null,
					group.getGroup_members().size()
					));
		});
		mapGroup.put("joined_groups", groupJoined);
		mapGroup.put("own_group", groupOwn);
		return mapGroup;
	}
	@Override
	public Optional<GroupDto> getGroup(Long id_user, Long id_group) {
		Optional<GroupDto> group = groupRep.findById(id_group).map((e) -> toDto(e));
		if(group.isPresent()) {
			Optional<GroupMember> checkMember = groupMemberRep.findMemberInGroupWithUser(id_group, id_user);
			if(checkMember.isPresent()&& checkMember.get().getRole()!=EGroupRole.MEMBER) {
				List<RequirementDto> requirements = new LinkedList<RequirementDto>();
				requireRep.findByGroup_id(id_group).forEach((req)->{ 
					RequirementDto reqDto = new RequirementDto();
					reqDto.set_accept(req.is_accept());
					reqDto.setId(req.getId());
					reqDto.setSender(mapper.map(req.getSender(), UserDto.class));
					reqDto.setGroup(group.get());
					reqDto.setType(req.getType().toString());
					reqDto.setCreate_at(req.getCreate_at());
					requirements.add(reqDto);});
				List<ReportDto> reports = new LinkedList<ReportDto>();
					reportRep.findByGroup_id(id_group).forEach((rep)->{
						ReportDto reportG = new ReportDto();
						reports.add(reportG);
					});;
				group.get().setRequirements(requirements);
				group.get().setReports_of_group(reports);
			}
			if(checkMember.isPresent() || group.get().isPublic_group()) {
				if(checkMember.isPresent()) {
					group.get().setMember_role(checkMember.get().getRole().toString());
				}
				group.get().setJoined(true);
				group.get().setMember_count(group.get().getGroup_members().size());
				List<GroupMemberDto> members = new LinkedList<GroupMemberDto>();
				groupMemberRep.findByGroup_id(id_group).forEach((gm)->{
					Optional<RelationshipUsers> relationship = relationshipRep.getRelationship(id_user, gm.getUser().getId());
					members.add(new GroupMemberDto(
							gm.getId(),
							gm.getUser().getId(),
							gm.getUser().getUsername(),
							gm.getUser().getImage()!=null?gm.getUser().getImage().getMedia():null,
							gm.getUser().getGender(),
							gm.getUser().getFullname(),
							gm.getGroup().getId(),
							gm.getGroup().getGroupname(),
							gm.getGroup().getImage_group()!=null? gm.getGroup().getImage_group().getMedia():null,
							true,
							gm.getRole().toString(),
							relationship.isPresent()? relationship.get().getType().toString():null
							));
				});
				List<GroupMediaDto> list_media = new LinkedList<GroupMediaDto>();
				groupMediaRep.findByGroup_id(id_group).forEach((media)->{
					list_media.add(new GroupMediaDto(
							media.getId(), 
							media.getMedia(), 
							media.getMedia_type(), 
							media.getCreate_at(), 
							media.getUser().getUsername(), 
							media.getUser().getFullname(),
							id_group, media.getPost().getId()));
				});
				group.get().setGroup_members(members);
				group.get().setPost_count(postRep.countPostsInGroup(id_group));
				group.get().setGroup_medias(list_media);
			}	
		}
		return group;
	}

	@Override
	public List<Object> getGroupUserJoined(Long id) {
		List<Object> list =  new LinkedList<Object>();
		groupRep.findGroupJoined(id).forEach((group)->{
			list.add(new GroupDto(
					group.getId(),
					group.getGroupname(),
					group.isPublic_group(),
					group.getDescription(),
					group.isActive(),
					group.getCreated_at(),
					group.getImage_group()!=null?group.getImage_group().getMedia():null,
					group.getBackground_group()!=null?group.getBackground_group().getMedia():null
					));
		});
		return list;
	}

}
