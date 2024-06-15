package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.annotation.Nullable;
import vn.aptech.beehub.models.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMeDto {
	private Long id;
	private String text;
	private String mediaUrl;
	private String background;
	private String color;
	private LocalDateTime createdAt;
	private Long user;
	private Long usershare;
	private String user_fullname;
	private Boolean share;
	private LocalDateTime timeshare;
	@Nullable
	private Long group ;
}
