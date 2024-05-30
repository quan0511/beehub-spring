package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryDto {
	private Long id;
	private Long user_id;
	private Long post_id;
	private String media;
	private String media_type;
	private LocalDateTime create_at;
	public GalleryDto(
			Long id,
			String media,
			String media_type) {
		this.id = id;
		this.media = media;
		this.media_type = media_type;
	}
}
