package vn.aptech.beehub.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeUserDto {
	private int id;
    private String enumEmo;
    private Long user;
    private String userUsername;
    private Long post;
	private String userImage;
	private String userGender;
	private String userFullname;
}
