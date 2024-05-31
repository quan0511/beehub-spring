package vn.aptech.beehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.aptech.beehub.models.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
	List<Group> findByGroupnameContains(String groupname);
	@Query(value = "SELECT g.* FROM groups g"
			+ " WHERE g.id IN (SELECT gm.group_id FROM group_members gm WHERE gm.user_id = ?1)"
			+ " AND g.active =1", nativeQuery = true)
	List<Group> findAllGroupJoined(Long id);
	@Query(value = "SELECT g.* FROM groups g"
			+ " WHERE g.id IN (SELECT gm.group_id FROM group_members gm WHERE gm.user_id = ?1 AND gm.role='GROUP_CREATOR')", nativeQuery = true)
	List<Group> findAllOwnGroup(Long id);
	@Query(value = "SELECT g.* FROM groups g WHERE g.id IN (SELECT gm.group_id FROM group_members gm WHERE gm.user_id = ?1)", nativeQuery = true)
	List<Group> findGroupJoined(Long id);
}
