package vn.aptech.beehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.aptech.beehub.models.RelationshipUsers;


@Repository
public interface RelationshipUsersRepository extends JpaRepository<RelationshipUsers, Integer> {
	@Query(value="SELECT * FROM relationship_users WHERE (ru.user1_id = ?1 OR ru.user2_id = ?1) AND ru.type = ?2", nativeQuery = true)
	List<RelationshipUsers> findByUser(Long id, String type);
	@Query(value="SELECT ru.* FROM relationship_users ru WHERE (ru.user1_id = ?1 AND ru.user2_id = ?2) OR (ru.user1_id = ?2 AND ru.user2_id = ?1)", nativeQuery = true)
	Optional<RelationshipUsers> getRelationship(Long id1, Long id2);	
	
	
}
