package vn.aptech.beehub.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
}
