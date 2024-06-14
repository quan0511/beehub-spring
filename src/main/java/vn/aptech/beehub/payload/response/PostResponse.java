package vn.aptech.beehub.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String creator;
    private Long creatorId;
    private String creatorImage;
    private LocalDateTime timestamp;
    private String content;
    private String image;
    private Boolean isBlocked;
}
