package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequirementDto {
	private Integer id;
	private Long sender_id;
	private UserDto sender;
	private Long receiver_id;
	private UserDto receiver;
	private Long group_id;
	private GroupDto group;
	private String type;
	private boolean is_accept;
	private LocalDateTime create_at;

}
