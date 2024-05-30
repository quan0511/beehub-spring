package vn.aptech.beehub.services;
import java.util.List;
import java.util.Map;

import vn.aptech.beehub.dto.UserSettingDto;


public interface IUserSettingService {
	public List<UserSettingDto> allSettingPostOfUser(Long id);
	public Map<String, String>  checkSettingPost(Long id);
	public Map<String, Integer> settingAllPost(Long id, String type);
	public void updateSettingItem(Long id, Map<String, String> item);
	public List<UserSettingDto> allSettingItemOfUser(Long id);
}
