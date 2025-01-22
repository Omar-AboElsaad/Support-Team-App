package com.Support.SupportTeam.Repository;

import com.Support.SupportTeam.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepo extends JpaRepository<Project,Long> {

    Optional<Project> findByName(String prjectName);

    List<Project> findByStartDate(Date startDate);

    List<Project> findByEndingDate(Date endingDate);

    void deleteByName(String projectName);

    List<Project> findBySupportMembers_Id(Long supportMemberId);

    @Query(value = "select * from project p where p.ending_date between current_date() and current_date()+10",nativeQuery = true)
    List<Project> findProjectsExpiringSoon();



}
