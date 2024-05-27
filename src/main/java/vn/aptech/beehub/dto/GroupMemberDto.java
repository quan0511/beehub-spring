package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDto {
	private int id;
	private Long user_id;
	private String username;
	private String user_image;
	private String user_gender;
	private String user_fullname;
	private Long group_id;
	private String group_name;
	private String group_image;
	private boolean joined;
	private String role;
	private String relationship;
	public GroupMemberDto(
			Long group_id,
			String group_name,
			String group_image,
			String role
			) {
		this.group_id = group_id;
		this.group_name = group_name;
		this.role = role;
	};
	public GroupMemberDto (int id,
			String username) {
		
	}
}
