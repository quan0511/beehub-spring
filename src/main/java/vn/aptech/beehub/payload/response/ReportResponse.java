package vn.aptech.beehub.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportResponse {
    private String from;
    private String to;
    private boolean isUser;
    private boolean isGroup;
    private boolean isPost;
    private String type;
    private LocalDateTime timeStamp;
    private String status;
}
