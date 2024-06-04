package vn.aptech.beehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportTypesDto {
	private Integer id;
	private String title;
	private String description;
	public ReportTypesDto(String title, String deString) {
		this.title = title;
		this.description = deString;
	}
}
