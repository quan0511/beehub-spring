package vn.aptech.beehub.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
	private Long id;
	private String text;
	@Nullable
	private List<GalleryDto> media;
	private Long user_id;
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
	
	public PostDto(
			String text,
			Long user_id,
			LocalDateTime create_at
			) {
		this.text = text;
		this.user_id = user_id;
		this.create_at = create_at;		
	}
	public PostDto(
			Long id, 
			String text, 
			List<GalleryDto> media, 
			Long user_id, 
			LocalDateTime create_at, 
			String user_fullname, 
			String user_image, 
			String user_gender, 
			String setting_type ) {
		this.id = id;
		this.text = text;
		this.media = media;
		this.user_id = user_id;
		this.create_at = create_at;
		this.user_fullname = user_fullname;
		this.user_image = user_image;
		this.user_gender = user_gender;
		this.setting_type=setting_type;
	}
	@Override
	public String toString() {
		return "Post "+this.id+": "+this.text+"\tMedia: "+this.media;
	}
//	public PostDto(Long id2, String text2, List<GalleryDto> media2, Long id3, Long long1, LocalDateTime create_at2,
//			String fullname, String username, Object object, String gender, Object object2, boolean b, Object object3,
//			Object object4, UserSettingDto userSettingToDto) {
//		// TODO Auto-generated constructor stub
//	}
}
