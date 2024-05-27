package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostMeDto {
	private Long id;
	private String text;
	//private MultipartFile media;
	private String mediaUrl;
	private String background;
	private String color;
	private String createdAt;
	private Long user;
}
