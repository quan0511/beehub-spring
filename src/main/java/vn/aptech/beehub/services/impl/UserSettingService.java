package vn.aptech.beehub.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.aptech.beehub.dto.UserSettingDto;
import vn.aptech.beehub.models.ESettingType;
import vn.aptech.beehub.models.Post;
import vn.aptech.beehub.models.UserSetting;
import vn.aptech.beehub.repository.PostRepository;
import vn.aptech.beehub.repository.UserRepository;
import vn.aptech.beehub.repository.UserSettingRepository;
import vn.aptech.beehub.services.IUserSettingService;

@Service
public class UserSettingService implements IUserSettingService {
	private Logger logger = LoggerFactory.getLogger(UserSettingService.class);
	@Autowired
	private UserSettingRepository userSettingRep;
	@Autowired
	private UserRepository userRep;
	@Autowired
	private PostRepository postRep;
	@Override
	public List<UserSettingDto> allSettingPostOfUser(Long id) {
		List<UserSettingDto> listSet = new LinkedList<UserSettingDto>();
		userSettingRep.findAllSettingPostOfUser(id).forEach((us)->{
			UserSettingDto userSetting = new UserSettingDto(us.getId(),us.getId(),us.getSetting_type().toString(),us.getPost().getId(), us.getSetting_item());
			listSet.add(userSetting);
		});
		return listSet;
	}
	@Override
	public Map<String, String> checkSettingPost(Long id) {
		List<UserSettingDto> listSet = allSettingPostOfUser(id);
		Map<String , String> res = new HashMap<>();
		if(listSet.size()>0) {
			List<String> check = new LinkedList<String>();
			listSet.forEach((e)->{
				if(!check.contains(e.getSetting_type())) {
					check.add(e.getSetting_type());					
				}
			});
			if(check.size()>1) {
				res.put("result", "OPTION");
			}else {
				res.put("result", check.get(0));				
			}
		}else {
			res.put("result", "PUBLIC");			
		}
		return res;
	}
	@Override
	public Map<String, Integer> settingAllPost(Long id, String type) {
		List<UserSetting> list =userSettingRep.findAllSettingPostOfUser(id);
		Integer count = 0;
		if(type!="OPTION") {
			for (UserSetting userSetting : list) {
				userSetting.setSetting_type(ESettingType.valueOf(type));
				userSettingRep.save(userSetting);
				count++;
			}
		}
		Map<String, Integer> res = new HashMap<String, Integer>();
		res.put("result", count);
		return res;
	}
	@Override
	public boolean updateSettingItem(Long id, Map<String, String> item) {
		List<UserSetting> findALl = userSettingRep.findByUser_id(id);
		boolean check = true;
		try {
			for (Iterator<UserSetting> iterator = findALl.iterator(); iterator.hasNext();) {
				UserSetting setting = (UserSetting) iterator.next();
				if(setting.getSetting_item()!=null && item.containsKey(setting.getSetting_item())) {
					setting.setSetting_type(ESettingType.valueOf(item.get(setting.getSetting_item())));
					userSettingRep.save(setting);
					check = false;
				};			
			}
			if(check) {
				UserSetting newSetting = new UserSetting();
				String[] keys = item.keySet().toArray(new String[item.size()]);

				newSetting.setSetting_item(keys[0]);
				newSetting.setSetting_type(ESettingType.valueOf(item.get(keys[0])));
				newSetting.setUser(userRep.findById(id).get());
				userSettingRep.save(newSetting);
			}			
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	@Override
	public List<UserSettingDto> allSettingItemOfUser(Long id) {
		List<UserSettingDto> result = new LinkedList<UserSettingDto>();
		userSettingRep.findSettingItemsOfUser(id).forEach((setting)->{
			UserSettingDto settingDto = new UserSettingDto(setting.getSetting_item(), setting.getSetting_type().toString());
			result.add(settingDto);
		});
		return result;
	}
	@Override
	public boolean updateSettingPost(Long id, UserSettingDto setting) {
		Optional<Post> findPost = postRep.findById(setting.getPost_id());
		if(findPost.isPresent()) {
			try {
				UserSetting setting_post = findPost.get().getUser_setting();
				setting_post.setSetting_type(ESettingType.valueOf(setting.getSetting_type()));
				userSettingRep.save(setting_post);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
