package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMediaDto {
	private Long id;
	private String media;
	private String media_type;
	private LocalDateTime create_at;
	private String username;
	private String fullname;
	private Long group_id;
	private Long post_id;
}
