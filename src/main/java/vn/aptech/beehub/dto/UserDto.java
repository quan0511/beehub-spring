package vn.aptech.beehub.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	private Long id;
	@NotEmpty(message = "Username can not be emty")
	private String username;
	@NotEmpty(message = "Fullname can not emty")
	private String fullname;
	@NotEmpty(message = "Gender is required")
	private String gender;
	@Nullable
	private String image;
	@Nullable
	private String image_type;
	@Nullable
	private String typeRelationship;
	private boolean is_banned;
	private int group_counter;
	private int friend_counter;
	public UserDto(Long id, String username, String fullname, String gender, String image, String image_type,String type,boolean is_banned) {
		this.id = id;
		this.username = username;
		this.fullname = fullname;
		this.gender = gender;
		this.image = image;
		this.image_type= image_type;
		this.typeRelationship = type;
		this.is_banned = is_banned;
	}
	public UserDto(Long id, String username, String fullname, String gender, String image, String image_type,boolean is_banned) {
		this.id = id;
		this.username = username;
		this.fullname = fullname;
		this.gender = gender;
		this.image = image;
		this.image_type= image_type;
		this.is_banned = is_banned;
	}
}
