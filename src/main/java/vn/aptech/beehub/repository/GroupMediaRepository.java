package vn.aptech.beehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.aptech.beehub.models.GroupMedia;

@Repository
public interface GroupMediaRepository extends JpaRepository<GroupMedia, Long>{
	List<GroupMedia> findByGroup_id(Long id);
}
