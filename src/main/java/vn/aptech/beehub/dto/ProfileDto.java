package vn.aptech.beehub.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {
	private Long id;
	private String username;
	private String email;
	private String fullname;
	private String gender;
	private String image;
	private String background;
	private String bio;
	private LocalDate birthday;
	private String phone;
	private boolean is_active;
	private String relationship_with_user;
	private LocalDateTime active_at;
	private boolean is_banned;
	private List<Object> group_joined;
	private List<UserSettingDto> user_settings;
	private List<UserDto> relationships;
	private List<PostDto> posts;
	private List<GalleryDto> galleries;
	@Override
	public String toString() {
		return "Username "+this.username+"\tFullname: "+this.fullname+"\tEmail: "+this.email+"\nGender: "+this.gender+"\tBirthday: "+this.birthday.toString()+"\tPhone: "+this.phone+"\nImage: "+this.image+"\nBackground: "+this.background;
	}
	public ProfileDto(Long id2, String username2, String email2, String fullname2, String gender2, String image,
			String bg, String bio2, LocalDate birthday2, String phone2,
			boolean is_active2) {
		this.id = id2;
		this.username = username2;
		this.email = email2;
		this.fullname = fullname2;
		this.gender = gender2;
		this.image =image;
		this.background = bg;
		this.bio = bio2;
		this.birthday = birthday2;
		this.phone = phone2;
	}
}
