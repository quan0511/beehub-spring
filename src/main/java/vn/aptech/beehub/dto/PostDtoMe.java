package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDtoMe {
	private int id;
	private String text;
	private String mediaUrl;
	private String background;
	private String color;
	private String createdAt;
	private int user;
}
