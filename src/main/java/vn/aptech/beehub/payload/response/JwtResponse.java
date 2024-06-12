package vn.aptech.beehub.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private Long id;
    private String token;
    private String username;
    private String type;
    private String email;
    private String image;
    private String background;
    private LocalDateTime createdAt;
    private List<String> roles;
}
