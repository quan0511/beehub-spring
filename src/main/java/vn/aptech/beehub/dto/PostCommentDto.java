package vn.aptech.beehub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDto {
	private int id;
	private String comment;
	private LocalDateTime createdAt;
	private int user;
	private int post;
}
