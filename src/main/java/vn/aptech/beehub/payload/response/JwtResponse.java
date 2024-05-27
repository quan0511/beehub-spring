package vn.aptech.beehub.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private Long id;
    private String token;
    private String username;
    private String type = "Bearer";
    private String refreshToken;
    private String email;
    private List<String> roles;

}
