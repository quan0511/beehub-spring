package vn.aptech.beehub.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.aptech.beehub.models.Report;


public interface ReportRepository extends JpaRepository<Report, Integer>{
	@Query(value = "SELECT r.* FROM reports r WHERE r.target_group_id=?1",nativeQuery = true)
	List<Report> findByGroup_id(Long id);
}
