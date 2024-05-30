package vn.aptech.beehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.aptech.beehub.models.GroupMember;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long>{
	@Query(value = "SELECT gm.* FROM group_members gm WHERE gm.group_id= ?1 AND gm.user_id=?2", nativeQuery = true)
	Optional<GroupMember> findMemberInGroupWithUser(Long id_group, Long id_user);
	
	List<GroupMember> findByGroup_id(Long id);
	List<GroupMember> findByUser_id(Long id);
}
