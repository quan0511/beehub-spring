package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
	private Long id;
	private String text;
	@Nullable
	private GalleryDto media;
	@Nullable
	private GroupMediaDto group_media;
	@Nullable
	private Long group_id;
	private LocalDateTime create_at;
	private String user_fullname;
	private String user_username;
	@Nullable
	private String user_image;
	private String user_gender;
	@Nullable
	private String group_name;
	@Nullable
	private boolean public_group;
	@Nullable
	private String group_image;
	private String setting_type;
	@Nullable
	private String color; 
	@Nullable
	private String background;
	private Long user_id; 
	
	public PostDto(
			Long id, 
			String text, 
			GroupMediaDto group_media,
			LocalDateTime create_at, 
			String user_username,
			String user_fullname, 
			String user_image, 
			String user_gender) {
		this.id = id;
		this.text = text;
		this.group_media = group_media;
		this.create_at = create_at;
		this.user_username = user_username;
		this.user_fullname = user_fullname;
		this.user_image = user_image;
		this.user_gender = user_gender;
	}
//	public PostDto(Long id2, String text2, List<GalleryDto> media2, Long id3, Long long1, LocalDateTime create_at2,
//			String fullname, String username, Object object, String gender, Object object2, boolean b, Object object3,
//			Object object4, UserSettingDto userSettingToDto) {
//		// TODO Auto-generated constructor stub
//	}
}
