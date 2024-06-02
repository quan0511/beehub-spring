package vn.aptech.beehub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeUserDto {
    private String enumEmo;
    private Long user;
}
