package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileFormDto {
	private Long id;
	private String username;
	private String email;
	private String fullname;
	private String gender;
	private String bio;
	private String birthday;
	private String phone;
	@Override
	public	String toString() {
		return "Id: "+id+"\tFullName: "+fullname+"";		
	}
}
