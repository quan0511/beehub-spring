package vn.aptech.beehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.aptech.beehub.models.ReportTypes;

@Repository
public interface ReportTypeRepository extends JpaRepository<ReportTypes, Integer> {

}
