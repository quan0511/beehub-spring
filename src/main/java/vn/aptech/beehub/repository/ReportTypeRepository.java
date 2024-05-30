package vn.aptech.beehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.aptech.beehub.models.ReportTypes;

@Repository
public interface ReportTypeRepository extends JpaRepository<ReportTypes, Integer> {
	@Query(value = "SELECT * FROM report_types ORDER BY RAND() LIMIT 1",nativeQuery = true)
	Optional<ReportTypes> getRandomReportType();
}
