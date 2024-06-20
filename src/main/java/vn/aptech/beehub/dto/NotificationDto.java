package vn.aptech.beehub.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.beehub.models.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
	private int id;
	private String content;
	private Long user;
	private Long post;
	private NotificationType notificationType;
	private boolean seen;
	private LocalDateTime createdAt;
}
