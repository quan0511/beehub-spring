package vn.aptech.beehub.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportResponse {
    private Integer id;
    private String reporter;
    private Long reporterId;
    private Long reportedCaseId;
    private String reportedCaseName;
    private String caseType;
    private String type;
    private LocalDateTime timestamp;
    private String status;
}
