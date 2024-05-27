package vn.aptech.beehub.services;
import java.util.List;

import vn.aptech.beehub.dto.UserSettingDto;


public interface IUserSettingService {
	public List<UserSettingDto> allSettingOfUser(Long id);
}
