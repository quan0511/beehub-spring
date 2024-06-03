package vn.aptech.beehub.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.User;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharePostDto {
	private int id;
	private Long originalPost;
    private Long sharedBy;
    private LocalDateTime sharedAt;
}
