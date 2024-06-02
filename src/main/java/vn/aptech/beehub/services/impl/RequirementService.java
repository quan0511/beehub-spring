package vn.aptech.beehub.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.SdkClientException;

import vn.aptech.beehub.aws.S3Service;
import vn.aptech.beehub.dto.RequirementDto;
import vn.aptech.beehub.models.EGroupRole;
import vn.aptech.beehub.models.ERelationshipType;
import vn.aptech.beehub.models.ERequirement;
import vn.aptech.beehub.models.Gallery;
import vn.aptech.beehub.models.Group;
import vn.aptech.beehub.models.GroupMedia;
import vn.aptech.beehub.models.GroupMember;
import vn.aptech.beehub.models.LikeUser;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.PostComment;
import vn.aptech.beehub.models.PostReaction;
import vn.aptech.beehub.models.RelationshipUsers;
import vn.aptech.beehub.models.Report;
import vn.aptech.beehub.models.Requirement;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.repository.GroupMediaRepository;
import vn.aptech.beehub.repository.GroupMemberRepository;
import vn.aptech.beehub.repository.GroupRepository;
import vn.aptech.beehub.repository.LikeRepository;
import vn.aptech.beehub.repository.PostCommentRepository;
import vn.aptech.beehub.repository.PostReactionRepository;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.RelationshipUsersRepository;
import vn.aptech.beehub.repository.ReportRepository;
import vn.aptech.beehub.repository.RequirementRepository;
import vn.aptech.beehub.repository.UserRepository;
import vn.aptech.beehub.repository.UserSettingRepository;
import vn.aptech.beehub.services.IRequirementService;
@Service
@Transactional
public class RequirementService implements IRequirementService {
	private Logger logger = LoggerFactory.getLogger(RequirementService.class);
	@Autowired
	private GroupRepository groupRep;
	@Autowired
	private RelationshipUsersRepository relationshipRep;
	@Autowired
	private RequirementRepository requirementRep;
	@Autowired
	private GroupMemberRepository groupMemberRep;
	@Autowired
	private UserRepository userRep;
	@Autowired
	private ReportRepository reportRep;
	@Autowired 
	private PostRepository postRep;
	@Autowired 
	private UserSettingRepository userSettingRep;
	@Autowired 
	private PostReactionRepository postReactRep;
	@Autowired
	private PostCommentRepository postComtRep;
	@Autowired
	private LikeRepository likeRep;
	@Autowired
	private GroupMediaRepository groupMediaRep;
	@Autowired
	private S3Service s3Service;
	@Override
	public Map<String, String> handleRequirement(Long id, RequirementDto requirement) {
		Map<String, String> result = new HashMap<String, String>();
		switch (requirement.getType()) {
		case "BLOCK":
			try {
				Optional<RelationshipUsers> relationship = relationshipRep.getRelationship(id, requirement.getReceiver_id());
				if(relationship.isPresent()&& relationship.get().getType().equals(ERelationshipType.FRIEND)) {
					relationshipRep.delete(relationship.get());
					RelationshipUsers newRelationship =  new RelationshipUsers();
					User sender = userRep.findById(id).get();
					User receiver = userRep.findById(requirement.getReceiver_id()).get();
					newRelationship.setUser1(sender);
					newRelationship.setUser2(receiver);
					newRelationship.setType(ERelationshipType.BLOCKED);
					relationshipRep.save(newRelationship);
					result.put("response",requirement.getType());
				}else if(relationship.isEmpty()) {
					RelationshipUsers blockRelationship =  new RelationshipUsers();
					User sender = userRep.findById(id).get();
					User receiver = userRep.findById(requirement.getReceiver_id()).get();
					blockRelationship.setUser1(sender);
					blockRelationship.setUser2(receiver);
					blockRelationship.setType(ERelationshipType.BLOCKED);
					relationshipRep.save(blockRelationship);
					result.put("response",requirement.getType());
				}else {
					result.put("response","unsuccess");
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "UN_BLOCK": 
			try {
				Optional<RelationshipUsers> relationship = relationshipRep.getRelationship(id, requirement.getReceiver_id());
				if(relationship.isPresent()&& relationship.get().getUser1().getId()==id && relationship.get().getType().equals(ERelationshipType.BLOCKED)) {
					relationshipRep.delete(relationship.get());
					result.put("response",requirement.getType());
				}else {
					result.put("response","unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "UN_FRIEND":
			try {
				Optional<RelationshipUsers> relationship = relationshipRep.getRelationship(id, requirement.getReceiver_id());
				if(relationship.isPresent() && relationship.get().getType().equals(ERelationshipType.FRIEND)) {
					relationshipRep.delete(relationship.get());
					result.put("response",requirement.getType());
				}else {
					result.put("response","unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "CANCEL_REQUEST":
			try {
				Optional<Requirement> req = requirementRep.getRequirementsBtwUsers(id, requirement.getReceiver_id());
				if(req.isPresent() && req.get().getType().equals(ERequirement.ADD_FRIEND) && req.get().getSender().getId()==id) {
					Requirement getReq = req.get();
					logger.info(getReq.getId().toString());
					
					requirementRep.delete(getReq);
					requirementRep.flush();
					result.put("response",requirement.getType());
				}else {
					result.put("response","unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "ACCEPT":
			try {
				Optional<Requirement> req = requirementRep.getRequirementsBtwUsers(id, requirement.getReceiver_id());
				if(req.isPresent() && req.get().getType().equals(ERequirement.ADD_FRIEND) && req.get().getReceiver().getId()==id) {
					Requirement getReq = req.get();
					RelationshipUsers newFriendship = new RelationshipUsers(getReq.getSender(),getReq.getReceiver(), ERelationshipType.FRIEND);
					relationshipRep.save(newFriendship);
					requirementRep.delete(req.get());
					result.put("response",requirement.getType());
				}else {
					result.put("response","unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "ADD_FRIEND":
			try {
				Requirement newReq = new Requirement();
				newReq.setSender(userRep.findById(id).get());
				newReq.setReceiver(userRep.findById(requirement.getReceiver_id()).get());
				newReq.setType(ERequirement.ADD_FRIEND);
				newReq.setCreate_at(LocalDateTime.now());
				newReq.set_accept(false);
				requirementRep.save(newReq);
				result.put("response",requirement.getType());
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "JOIN":
			try {
				Requirement newReq = new Requirement();
				newReq.setSender(userRep.findById(id).get());
				newReq.setGroup_receiver(groupRep.findById(requirement.getGroup_id()).get());
				newReq.setType(ERequirement.JOIN_GROUP);
				newReq.setCreate_at(LocalDateTime.now());
				newReq.set_accept(false);
				requirementRep.save(newReq);
				result.put("response",requirement.getType());
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "ACCEPT_MEMBER":
			try {
				Optional<Requirement> findReq = requirementRep.findRequirementJoinGroup(requirement.getReceiver_id(), requirement.getGroup_id());
				if(findReq.isPresent()) {
					Requirement acceptReq = findReq.get();
					requirementRep.delete(acceptReq);
					GroupMember newMem = new GroupMember();
					newMem.setGroup(groupRep.findById(requirement.getGroup_id()).get());
					newMem.setUser(userRep.findById(requirement.getReceiver_id()).get());
					newMem.setRole(EGroupRole.MEMBER);
					groupMemberRep.save(newMem);
					result.put("response",requirement.getType());
				}else {
					result.put("response","unsuccess");				
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "REJECT":
			try {
				Optional<Requirement> findReq = requirementRep.findRequirementJoinGroup(requirement.getReceiver_id(), requirement.getGroup_id());
				if(findReq.isPresent()) {
					Requirement acceptReq = findReq.get();
					requirementRep.delete(acceptReq);
					result.put("response",requirement.getType());
				}else {
					result.put("response","unsuccess");				
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "CANCEL_JOIN":
			try {
				Optional<Requirement> req = requirementRep.findRequirementJoinGroup(id, requirement.getGroup_id());
				if(req.isPresent() && !req.get().is_accept() && req.get().getSender().getId()==id) {
					requirementRep.delete(req.get());
					logger.info("Require "+requirement.getGroup_id()+" \t User: "+requirement.getSender_id());
					result.put("response",requirement.getType());
				}else {
					result.put("response","unsuccess");		
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response","error");
			}
			break;
		case "OUT_GROUP":
			try {
				Optional<GroupMember> groupMember = groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(), id);
				if(groupMember.isPresent() && !groupMember.get().getRole().equals(EGroupRole.GROUP_CREATOR)) {
					groupMemberRep.delete(groupMember.get());
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "KICK":
			try {
				Optional<GroupMember> groupMem= groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(),requirement.getReceiver_id());
				if(groupMem.isPresent()&& !groupMem.get().getRole().equals(EGroupRole.GROUP_CREATOR)) {
					groupMemberRep.delete(groupMem.get());
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "LEAVE_GROUP":
			try {
				Optional<GroupMember> groupMem= groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(),id);
				if(groupMem.isPresent() && !groupMem.get().getRole().equals(EGroupRole.GROUP_CREATOR)) {
					groupMemberRep.delete(groupMem.get());
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "SET_MANAGER":
			try {
				Optional<GroupMember> groupMem= groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(), requirement.getReceiver_id());
				if(groupMem.isPresent()&&groupMem.get().getRole().equals(EGroupRole.MEMBER)){
					GroupMember getGroupMember= groupMem.get();
					getGroupMember.setRole(EGroupRole.GROUP_MANAGER);
					groupMemberRep.save(getGroupMember);
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "REMOVE_MANAGER":
			try {
				Optional<GroupMember> groupMem= groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(), requirement.getReceiver_id());
				if(groupMem.isPresent()&& groupMem.get().getRole().equals(EGroupRole.GROUP_MANAGER)) {
					GroupMember getGroupMem = groupMem.get();
					getGroupMem.setRole(EGroupRole.MEMBER);
					groupMemberRep.save(getGroupMem);
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "TOGGLE_ACTIVE_GROUP":
			try {
				Optional<GroupMember> groupMem= groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(), id);
				if(groupMem.isPresent()&& groupMem.get().getRole().equals(EGroupRole.GROUP_CREATOR)) {
					Group getGroup = groupMem.get().getGroup();
					getGroup.setActive(!getGroup.isActive());
					groupRep.save(getGroup);
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "TOGGLE_PUBLIC_GROUP":
			try {
				Optional<GroupMember> groupMem= groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(), id);
				if(groupMem.isPresent()&& groupMem.get().getRole().equals(EGroupRole.GROUP_CREATOR)) {
					Group getGroup = groupMem.get().getGroup();
					getGroup.setPublic_group(!getGroup.isPublic_group());
					groupRep.save(getGroup);
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "ACCEPT_REPORT":
			try {
				Optional<GroupMember> groupMem= groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(), id);
				logger.info(groupMem.get().getRole().toString());
				if(groupMem.isPresent() && groupMem.get().getRole().equals(EGroupRole.GROUP_CREATOR)) {
					Optional<Report> findReport = reportRep.findById(requirement.getReport_id());
					if(findReport.isPresent() && findReport.get().getTarget_group().getId() == requirement.getGroup_id()) {
						Report getReport = findReport.get();
						logger.info(getReport.getTarget_post().getId().toString());		
						Optional<Post> findPost = postRep.findById(getReport.getTarget_post().getId());
						if(findPost.isPresent()) {
							Post getPost = findPost.get();
				            List<PostComment> comment = getPost.getComments();
				            postComtRep.deleteAll(comment);
				            List<PostReaction> recomment = getPost.getReactions();
				            postReactRep.deleteAll(recomment);
				            List<LikeUser> like = getPost.getLikes();
				            likeRep.deleteAll(like);
				            if(getPost.getGroup_media()!=null) {
				            	String filename = getPost.getGroup_media().getMedia();
				            	String fileExtract = filename!=null? filename.substring(filename.lastIndexOf("/") + 1):null;
				            	if(fileExtract !=null) {
				            		s3Service.deleteToS3(fileExtract);
				            	}
				            	GroupMedia gallery = getPost.getGroup_media();
				            	groupMediaRep.delete(gallery);				            	
				            }
				            reportRep.deletePostReposts(getPost.getId());
							postRep.deletePost(getPost.getId());
						}
						
						result.put("response", requirement.getType());						
					}else {
						result.put("response", "unsuccess");
					}
				}else {
					result.put("response", "unsuccess");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "CANCEL_REPORT":
			try {
				Optional<GroupMember> groupMem= groupMemberRep.findMemberInGroupWithUser(requirement.getGroup_id(), id);
				if(groupMem.isPresent() && groupMem.get().getRole().equals(EGroupRole.GROUP_CREATOR)) {
					Optional<Report> findReport = reportRep.findById(requirement.getReport_id());
					if(findReport.isPresent() && findReport.get().getTarget_group().getId() == requirement.getGroup_id()) {
						reportRep.deleteReport(requirement.getReport_id());
						result.put("response", requirement.getType());
					}else {
						result.put("response", "unsuccess");
					}
				}else {
					result.put("response", "unsuccess");
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.put("response", "error");
			}
			break;
		case "DEACTIVE_ACCOUNT":
			try {
				Optional<User> findUser= userRep.findById(requirement.getReceiver_id());
				if(findUser.isPresent() && requirement.getReceiver_id()== id) {
					User getUser = findUser.get();
					getUser.set_active(false);
					userRep.save(getUser);
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				result.put("response", "error");
			}
			break;
		case "ACTIVE_ACCOUNT":
			try {
				Optional<User> findUser= userRep.findById(requirement.getReceiver_id());
				if(findUser.isPresent() && requirement.getReceiver_id()== id) {
					User getUser = findUser.get();
					getUser.set_active(true);
					userRep.save(getUser);
					result.put("response", requirement.getType());
				}else {
					result.put("response", "unsuccess");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				result.put("response", "error");
			}
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + requirement.getType());
		}
		return result;
	}
}
