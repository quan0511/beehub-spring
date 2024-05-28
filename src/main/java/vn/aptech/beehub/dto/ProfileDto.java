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
	private boolean email_verified;
	private String phone;
	private boolean is_active;
	private LocalDateTime active_at;
	private LocalDateTime create_at;
	private List<Object> group_joined;
	private List<UserSettingDto> user_settings;
	private List<UserDto> relationships;
	private List<PostDto> posts;
	private List<GalleryDto> galleries;
		
}
