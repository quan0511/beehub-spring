package vn.aptech.beehub.payload.response;

import lombok.Data;

@Data
public class GetMessageResponse {
    private int id;
    private String messageBody;
    private Long creatorId;
    private boolean isRead;
}
