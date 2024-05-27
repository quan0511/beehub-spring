package vn.aptech.beehub.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
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
	private int group_counter;
	private int friend_counter;
	public UserDto(Long id, String username, String fullname, String gender, String image, String image_type,String type) {
		this.id = id;
		this.username = username;
		this.fullname = fullname;
		this.gender = gender;
		this.image = image;
		this.image_type= image_type;
		this.typeRelationship = type;
		
	}
	public UserDto(Long id, String username, String fullname, String gender, String image, String image_type) {
		this.id = id;
		this.username = username;
		this.fullname = fullname;
		this.gender = gender;
		this.image = image;
		this.image_type= image_type;
	}
}
