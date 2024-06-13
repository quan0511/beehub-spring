package vn.aptech.beehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.aptech.beehub.models.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @Query(value = "SELECT *  FROM users u "
			+ " WHERE (u.id IN (SELECT ru.user2_id FROM relationship_users  ru WHERE ru.user1_id = ?1 AND ru.user2_id <> ?1 AND ru.type = ?2)"
			+ " OR u.id IN (SELECT ru.user1_id FROM relationship_users  ru WHERE ru.user2_id = ?1 AND ru.user1_id <> ?1 AND ru.type = ?2)) AND u.is_active=1 AND u.is_banned=0", nativeQuery = true)
	List<User> findRelationship(Long id,String type);
    
    @Query(value = "SELECT *  FROM users u "
			+ " WHERE (u.id IN (SELECT ru1.user2_id FROM relationship_users  ru1 WHERE ru1.user1_id = ?2 AND ru1.user2_id <> ?2 AND ru1.type = 'FRIEND')"
			+ " OR u.id IN (SELECT ru2.user1_id FROM relationship_users  ru2 WHERE ru2.user2_id = ?2 AND ru2.user1_id <> ?2 AND ru2.type = 'FRIEND')) "
			+ " AND u.is_active=1 AND u.is_banned=0 "
			+ " AND u.id NOT IN (SELECT ru3.user1_id FROM relationship_users  ru3 WHERE ru3.user2_id = ?1 AND ru3.user1_id <> ?1 AND ru3.type = 'BLOCKED')", nativeQuery = true)
	List<User> findProfileRelationship(Long id_user, Long id_profile);
    
    @Query(value = "SELECT *  FROM users u "
			+ " WHERE u.id IN (SELECT ru.user2_id FROM relationship_users  ru WHERE ru.user1_id = ?1 AND ru.user2_id <> ?1 AND ru.type = 'BLOCKED') AND u.is_active=1 AND u.is_banned=0", nativeQuery = true)
	List<User> findBlocked(Long id);
    
	Optional<User> findByUsername(@Param("username") String username);
	//Searching People by username or fullname
	@Query(value = "SELECT * FROM users u WHERE ( u.username LIKE CONCAT('%',:search,'%') OR u.fullname LIKE CONCAT('%',:search,'%')) AND u.id <> :id AND u.is_active=1 AND u.is_banned=0", nativeQuery = true)
	List<User> searchPeople( @Param("search") String search, @Param("id") Long id);
	
	@Query(value = "SELECT DISTINCT u.* FROM users u"
			+ " LEFT JOIN group_members gm ON gm.user_id = u.id"
			+ " WHERE u.is_active=1 AND u.is_banned=0 AND (gm.group_id IN (SELECT gm2.group_id FROM group_members gm2 WHERE gm2.user_id=?1) "
			+ " AND u.id NOT IN (SELECT u2.id FROM users u2 LEFT JOIN relationship_users ru ON ru.user1_id =u2.id OR ru.user2_id = u2.id WHERE (ru.user1_id = ?1 OR ru.user2_id= ?1) AND u2.id <> ?1 )"
			+ " AND u.id <> ?1)", nativeQuery = true)
	List<User> findPeopleSameGroup(Long id);
	
	@Query(value="SELECT u.* FROM users u"
			+ " WHERE u.id IN (SELECT r.receiver_id FROM requirements r WHERE r.sender_id = ?1 AND r.group_id IS NULL) AND u.is_active=1 AND u.is_banned=0", nativeQuery = true)
	List<User> findPeopleUserSendAddFriend(Long id);
	@Query(value = "SELECT * FROM users WHERE id IN (SELECT u.id FROM users u WHERE u.id NOT IN (SELECT gm.user_id FROM group_members gm WHERE gm.group_id = ?1))  ORDER BY RAND() LIMIT 2",  nativeQuery = true)
	List<User> findUsersNotJoinedGroup(Long id_group);
	
}
