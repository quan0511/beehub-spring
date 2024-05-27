package vn.aptech.beehub.services.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.aptech.beehub.dto.UserSettingDto;
import vn.aptech.beehub.repository.UserSettingRepository;
import vn.aptech.beehub.services.IUserSettingService;

@Service
public class UserSettingService implements IUserSettingService {
	@Autowired
	private UserSettingRepository userSettingRep;
	@Override
	public List<UserSettingDto> allSettingOfUser(Long id) {
		List<UserSettingDto> listSet = new LinkedList<UserSettingDto>();
		userSettingRep.findAllSettingOfUser(id).forEach((us)->{
			UserSettingDto userSetting = new UserSettingDto(us.getId(),us.getId(),us.getSetting_type().toString(),us.getPost().getId(), us.getSetting_item());
			listSet.add(userSetting);
		});
		return listSet;
	}

}
