package vn.aptech.beehub.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private int numOfUsers;
    private int numOfGroups;
    private int numOfPosts;
    private int numOfReports;
}
