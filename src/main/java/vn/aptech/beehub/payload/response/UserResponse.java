package vn.aptech.beehub.payload.response;

import lombok.Builder;
import lombok.Data;
import vn.aptech.beehub.models.Report;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String gender;
    private int noOfPosts;
    private int noOfFriends;
    private List<String> reportTitleList;
    private String role;
    private String status;
    private String avatar;
    private List<String> gallery;
    private LocalDateTime createdAt;
}
