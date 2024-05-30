package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
	private Integer id;
	private UserDto sender;
	private UserDto target_user;
	private GroupDto target_group;
	private PostDto target_post;
	private ReportTypesDto type;
	private String add_description;
	private LocalDateTime create_at;
	private LocalDateTime update_at;
	
}
