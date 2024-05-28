package vn.aptech.beehub.payload.response;

import lombok.Data;

@Data
public class TokenRefreshResponse {
    private String token;
    private String tokenType = "Bearer";

    public TokenRefreshResponse(String token) {
        this.token = token;
    }
}
