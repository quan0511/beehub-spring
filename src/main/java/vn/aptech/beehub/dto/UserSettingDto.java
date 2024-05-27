package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingDto {
	private Long id;
	private Long user_id;
	private String setting_type;
	private Long post_id;
	private String setting_item;
	public UserSettingDto(Long user_id,String setting_type) {
		this.user_id = user_id;
		this.setting_type = setting_type;
	}
}
