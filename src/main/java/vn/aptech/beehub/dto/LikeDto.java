package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
	private int id;
	private int post;
	private int user;
	private String enumEmo;
}
