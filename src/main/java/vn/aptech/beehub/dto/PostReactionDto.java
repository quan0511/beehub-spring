package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostReactionDto {
	private int id;
	private String reaction;
	private LocalDateTime createdAt;
	private Long post;
	private String username;
	private Long user;
	private int postComment;
}
