package vn.aptech.beehub.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.beehub.models.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private boolean isPublic;
    private String name;
    private Long creatorId;
    private String creatorUsername;
    private String creatorImage;
    private int noOfMembers;
    private boolean isActive;
    private LocalDateTime createdAt;
    private String background;
    private List<String> gallery;
    private List<String> reportTitleList;
}
