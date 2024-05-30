package vn.aptech.beehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.aptech.beehub.models.UserSetting;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long>{
	@Query(value = "SELECT * FROM user_setting us WHERE us.user_id=?1 AND us.id IN (SELECT p.setting_id FROM posts p WHERE p.group_id IS NULL)",nativeQuery = true)
	List<UserSetting> findAllSettingPostOfUser(Long id);
	
	List<UserSetting> findByUser_id(Long id);
	@Query(value = "SELECT * FROM user_setting us WHERE us.user_id=?1 AND us.id NOT IN (SELECT p.setting_id FROM posts p)", nativeQuery = true)
	List<UserSetting> findSettingItemsOfUser(Long id);
}
