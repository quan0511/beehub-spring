package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportFormDto {
	private Long sender_id;
	private String user_username;
	private Long target_group_id;
	private Long target_post_id;
	private Integer type_id;
	private String add_description;
}
