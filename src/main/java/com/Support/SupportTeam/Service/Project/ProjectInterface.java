package com.Support.SupportTeam.Service.Project;

import com.Support.SupportTeam.DTO.ProjectDTO;
import com.Support.SupportTeam.Entity.Company;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.ProjectRequest;
import com.Support.SupportTeam.Request.UpdateProjectRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectInterface {
    Page<Project> findAllProject(int page, int pageSize);

    //----------------------------------------------------------------------------------------------------------------------
    Project findById(Long id);

    List<Project> SearchProjectByName(String projectName);

    //----------------------------------------------------------------------------------------------------------------------
    Project findProjectByName(String projectName);

    //----------------------------------------------------------------------------------------------------------------------
    List<Project> findProjectByStartingDate(LocalDate startDate);

    //----------------------------------------------------------------------------------------------------------------------
    List<Project> findProjectByEndingDate(LocalDate endingDate);

    //----------------------------------------------------------------------------------------------------------------------
    List<Project> findAllUserProject(User user);

    //----------------------------------------------------------------------------------------------------------------------
    Project addProject(ProjectRequest request);

    Project addCompanyToProject(String companyName, String projectName);

    //----------------------------------------------------------------------------------------------------------------------
        @Transactional
        void deleteById(Long id);

    //----------------------------------------------------------------------------------------------------------------------
    Project updateProject(UpdateProjectRequest request);

    //---------------------------------------------------------------------------------------------------------------------
    void CheckIsProjectExistWithDifferentName(UpdateProjectRequest request);
    //---------------------------------------------------------------------------------------------------------------------

    int calcNumberOfChangeEndDate(UpdateProjectRequest request, Project project);
    //---------------------------------------------------------------------------------------------------------------------

    //Helper Method to valid if a project already exists or not

    Company checkForProjectCompany(UpdateProjectRequest request, Project project);
    //---------------------------------------------------------------------------------------------------------------------

    @Transactional
    void deleteByName(String name);

    void delete();
    //---------------------------------------------------------------------------------------------------------------------

    List<Project> getAllExpiredProjects();
    //---------------------------------------------------------------------------------------------------------------------

    long getNumberOfExpiredProjects();
    //---------------------------------------------------------------------------------------------------------------------

    long getNumberOfActiveProjects();
    //---------------------------------------------------------------------------------------------------------------------

    Optional<Project> findProjectByNameOrNull(String projectName);


}
