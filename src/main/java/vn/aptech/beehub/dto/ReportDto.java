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
	private Long sender_id;
	private String sender_username;
	private String sender_fullname;
	private String sender_image;
	private String sender_gender;
	private Long target_user_id;
	private String target_user_username;
	private String target_user_fullname;
	private String target_user_image;
	private String target_user_gender;
	private Long target_group_id;
	private String group_name;
	private String group_image;
	private Long target_post_id;
	private ReportTypesDto type;
	private String add_description;
	private LocalDateTime create_at;
	private LocalDateTime update_at;
	
//	public ReportDto(
//			Long sender_id,
//			Integer type_id,
//			LocalDateTime create_at,
//			LocalDateTime update_at) {
//		this.sender_id = sender_id;
//		this.type_id =type_id;
//		this.create_at = create_at;
//		this.update_at =update_at;
//	}
}
