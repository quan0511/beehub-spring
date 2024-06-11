package vn.aptech.beehub.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
	private Long id;
	private String groupname;
	private boolean public_group;
	private String description;
	private boolean active;
	private LocalDateTime created_at;
	private String image_group;
	private String background_group;
	private String joined;
	@Nullable
	private String member_role;
	private int member_count;
	private int post_count;
	@Nullable
	private List<RequirementDto> requirements;
	@Nullable
	private List<GroupMemberDto> group_members;
	@Nullable
	private List<GroupMediaDto> group_medias;
	@Nullable
	private List<ReportDto> reports_of_group;
	
	public GroupDto(
			String groupname,
			String description	
			){
		this.groupname = groupname;
		this.description = description;
		this.created_at = LocalDateTime.now();
	}
	public GroupDto(
			String groupname,
			String description,
			String image_group,
			LocalDateTime create_at,
			boolean public_group,
			String joined
			){
		this.groupname = groupname;
		this.description = description;
		this.image_group = image_group;
		this.created_at = create_at;
		this.public_group = public_group;
		this.joined = joined;
		

	}
	public GroupDto(Long id, 
			String groupname,
			boolean public_group,
			String description,
			boolean active,
			LocalDateTime created_at,
			String image_group,
			String background_group,
			String joined,
			String member_role,
			int member_count) {
		this.id = id;
		this.groupname = groupname;
		this.public_group = public_group;
		this.description = description;
		this.active = active;
		this.created_at = created_at;
		this.image_group = image_group;
		this.background_group = background_group;
		this.joined =joined;
		this.member_role = member_role;
		this.member_count = member_count;
	}
	public GroupDto(Long id2, 
			String groupname2, 
			boolean public_group2, 
			String description2, 
			boolean active2,
			LocalDateTime created_at2, 
			String image, String background) {
		this.id = id2;
		this.groupname = groupname2;
		this.public_group = public_group2;
		this.description = description2;
		this.active = active2;
		this.created_at =created_at2;
		this.image_group = image;
		this.background_group = background;
	}
	public GroupDto(
			Long id, 
			String groupname, 
			boolean active, 
			String image) {
		this.id = id;
		this.groupname =groupname;
		this.active = active;
		this.image_group = image;
	}
}
