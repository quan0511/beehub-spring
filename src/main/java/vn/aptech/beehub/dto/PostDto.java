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
	@Nullable
    private Boolean share; 
    @Nullable
    private String medias;
    @Nullable
    private String usershare_fullname;
    @Nullable
    private String usershare_username;
    @Nullable
    private String usershare_gender;
    @Nullable
    private String usershareimage;
    @Nullable
    private String usershareGroupName;
    @Nullable
    private Long usershareGroupId;
    @Nullable
    private LocalDateTime timeshare;
    private boolean is_blocked;
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
	public PostDto(
			Long id,
			String text,
			GalleryDto media,
			LocalDateTime create_at,
			String user_fullname,
			String user_username,
			String user_image,
			String user_gender,
			String setting_type,
			String color,
			String background,
			Long user_id,
			Boolean share,
			String medias,
			String usershare_fullname,
			String usershare_username,
			String usershare_gender,
			String usershareimage,
			String usershareGroupName,
			Long usershareGroupId,
			LocalDateTime timeshare,
			boolean is_blocked
			) {
		this.id = id;
		this.text  =text;
		this.media = media;
		this.create_at =create_at;
		this.user_fullname = user_fullname;
		this.user_username = user_username;
		this.user_image =user_image;
		this.user_gender = user_gender;
		this.setting_type = setting_type;
		this.color =color;
		this.background = background;
		this.user_id =user_id;
		this.share = share;
		this.medias = medias;
		this.usershare_fullname = usershare_fullname;
		this.usershare_username = usershare_username;
		this.usershare_gender = usershare_gender;
		this.usershareimage = usershareimage;
		this.usershareGroupName = usershareGroupName;
		this.usershareGroupId = usershareGroupId;
		this.timeshare = timeshare;
		this.is_blocked = is_blocked;
	}
	

}
