package vn.aptech.beehub.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchingDto {
	private List<PostDtoMe> posts;
	private List<UserDto> people;
	private List<GroupDto> groups;
}
