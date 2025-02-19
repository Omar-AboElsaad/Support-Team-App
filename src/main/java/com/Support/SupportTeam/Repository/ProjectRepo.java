package com.Support.SupportTeam.Repository;

import com.Support.SupportTeam.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepo extends JpaRepository<Project,Long> {

    Optional<Project> findByName(String projectName);

    List<Project> findByStartDate(LocalDate startDate);

    List<Project> findByEndingDate(LocalDate endingDate);

    void deleteByName(String projectName);

    List<Project> findBySupportMembers_Id(Long supportMemberId);

    @Query(value = "select * from project p where p.ending_date between current_date() and current_date()+10",nativeQuery = true)
    List<Project> findProjectsExpiringSoon();

    @Query(value = "select * from project p where p.ending_date < current_date()",nativeQuery = true)
    List<Project> findExpiredProjects();

    @Query("SELECT COUNT(p) FROM Project p WHERE p.endingDate < CURRENT_DATE")
    long countExpiredProjects();

    @Query("SELECT COUNT(p) FROM Project p WHERE p.endingDate > CURRENT_DATE")
    long countActiveProjects();

    @Query("SELECT p FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchValue, '%'))")
    List<Project> searchByProjectName(@Param("searchValue") String searchValue);




}
