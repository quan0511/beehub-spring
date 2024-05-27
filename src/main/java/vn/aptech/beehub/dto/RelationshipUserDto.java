package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipUserDto {
	private Integer id;
	private Long user1_id;
	private Long user2_id;
	private String type;
	public RelationshipUserDto(
			Long user1_id,Long user2_id, String type
			) {
		this.user1_id = user1_id;
		this.user2_id = user2_id;
		this.type = type;
	}
}
