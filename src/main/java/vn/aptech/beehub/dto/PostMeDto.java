package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMeDto {
	private Long id;
	private String text;
	//private MultipartFile media;
	private String mediaUrl;
	private String background;
	private String color;
	private LocalDateTime createdAt;
	private Long user;
}
