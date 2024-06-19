package vn.aptech.beehub.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMessageResponse {
    private int id;
    private String messageBody;
    private Long creatorId;
    private Long recipientId;
    private String creatorName;
    private String creatorAvatar;
    private boolean isRead;
    private LocalDateTime createdAt;
}
