package vn.aptech.beehub.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminProfile {
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
}
