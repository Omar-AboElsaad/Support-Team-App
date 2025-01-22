package com.Support.SupportTeam.Service;


import com.Support.SupportTeam.DTO.ProjectDTO;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Repository.ProjectRepo;
import com.Support.SupportTeam.Request.ProjectRequest;
import com.Support.SupportTeam.Request.UpdateProjectRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepo projectRepo;
    private final ModelMapper modelMapper;

    public List<Project> findAllProject(){
        List<Project>projects= projectRepo.findAll();
        if (projects.isEmpty())
            throw new RuntimeException("There is no project ");
        return projects;
    }


//----------------------------------------------------------------------------------------------------------------------

    public Project findProjectByName(String projectName){

        return projectRepo.findByName(projectName)
                .orElseThrow(() -> new RuntimeException("There is no project with name "+projectName));
    }
//----------------------------------------------------------------------------------------------------------------------

    public List<Project> findProjectByStartingDate(Date startDate){

        List<Project> projects= projectRepo.findByStartDate(startDate);
        if (projects.isEmpty())throw new RuntimeException("There is no project start at "+startDate);
        return projects;

    }

//----------------------------------------------------------------------------------------------------------------------

    public List<Project> findProjectByEndingDate(Date endingDate) {
        List<Project> projects=projectRepo.findByEndingDate(endingDate);
        if (projects.isEmpty())throw new RuntimeException("There is no project ending at "+endingDate);
        return projects;
    }

//----------------------------------------------------------------------------------------------------------------------

    public List<Project> findAllUserProject(User user) {
        List<Project> projects=  projectRepo.findBySupportMembers_Id(user.getId());
        if (projects.isEmpty())
            throw new RuntimeException(user.getFirstName()+" did not have projects ");
        return projects;
    }


//----------------------------------------------------------------------------------------------------------------------

    public Project addProject(ProjectRequest request){

        if (request.getStartDate().after(request.getEndingDate())){
            throw new RuntimeException("Start date must be before end date");
        } else if (findProjectByNameOrNull(request.getName()).isPresent()) {
            throw new RuntimeException(request.getName()+" Already Added");
        }

        Project project= Project.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endingDate(request.getEndingDate())
                .build();

       return projectRepo.save(project);
    }

//----------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void deleteByName(String ProjectName){
        findProjectByName(ProjectName);
        projectRepo.deleteByName(ProjectName);
    }

//----------------------------------------------------------------------------------------------------------------------

    public Project updateProject(UpdateProjectRequest request){
        Project project=projectRepo.getReferenceById(request.getId());
        Optional<Project> project1 =findProjectByNameOrNull(request.getName());

        if (project1.isPresent()&&!request.getId().equals(project1.get().getId())){
            throw new RuntimeException("There is already project with this name ," +
                    " you can modify it by use id --> "+project1.get().getId());
        }

        project.setName(request.getName());
        project.setStartDate(request.getStartDate());
        project.setEndingDate(request.getEndingDate());

       return projectRepo.save(project);

    }

//----------------------------------------------------------------------------------------------------------------------

    public void delete(){
        projectRepo.deleteAll();
    }

//----------------------------------------------------------------------------------------------------------------------

    //Helper Method to valid if a project already exists or not
    private Optional<Project> findProjectByNameOrNull(String projectName){
        return projectRepo.findByName(projectName);
    }


    public ProjectDTO ConvertProjectToProjectDto(Project project) {
        return modelMapper.map(project, ProjectDTO.class);
    }

}
