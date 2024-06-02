package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharePostDto {
	private int id;
	private Long post;
	private Long user;
}
