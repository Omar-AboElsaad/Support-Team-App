package com.Support.SupportTeam.Service.Project;


import com.Support.SupportTeam.CustomExceptions.InvalidInputException;
import com.Support.SupportTeam.CustomExceptions.NoProjectsFoundException;
import com.Support.SupportTeam.CustomExceptions.ResourceAlreadyExistException;
import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.ProjectDTO;
import com.Support.SupportTeam.Entity.Company;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Repository.CompanyRepo;
import com.Support.SupportTeam.Repository.ProjectRepo;
import com.Support.SupportTeam.Request.ProjectRequest;
import com.Support.SupportTeam.Request.UpdateProjectRequest;
import com.Support.SupportTeam.Service.CompanyService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProjectService implements ProjectInterface {
    private final ProjectRepo projectRepo;
    private final CompanyService companyService;
    private final ModelMapper modelMapper;

    @Override
    public Page<Project> findAllProject(int page, int pageSize){
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Project> projects= projectRepo.findAll(pageable);

        if (projects.isEmpty())
            throw new NoProjectsFoundException("There is no project ");
        return projects;
    }

//----------------------------------------------------------------------------------------------------------------------
    @Override
    public Project findById(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("There is no project with this id"));
    }


//----------------------------------------------------------------------------------------------------------------------

    @Override
    public List<Project> SearchProjectByName(String projectName){

        List<Project> projects=projectRepo.searchByProjectName(projectName);
        if (projects.isEmpty()){
           throw new ResourceNotFoundException ("There is no project with name "+projectName);
        }
        return projects;
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Project findProjectByName(String projectName){

       return projectRepo.findByName(projectName)
               .orElseThrow(() ->
                       new ResourceNotFoundException("There is no Projects with name "+projectName));

    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Project> findProjectByNameOrNull(String projectName){
        return projectRepo.findByName(projectName);
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public List<Project> findProjectByStartingDate(LocalDate startDate){

        List<Project> projects= projectRepo.findByStartDate(startDate);
        if (projects.isEmpty())throw new NoProjectsFoundException("There is no project start at "+startDate);
        return projects;

    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public List<Project> findProjectByEndingDate(LocalDate endingDate) {
        List<Project> projects=projectRepo.findByEndingDate(endingDate);
        if (projects.isEmpty())throw new NoProjectsFoundException("There is no project ending at "+endingDate);
        return projects;
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public List<Project> findAllUserProject(User user) {
        List<Project> projects=  projectRepo.findBySupportMembers_Id(user.getId());
        if (projects.isEmpty())
            throw new NoProjectsFoundException(user.getFirstName()+" did not have projects ");
        return projects;
    }
    
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Project addProject(ProjectRequest request){
        Company company;

        ValidateRequestDateAndExisting(request);

        company = getCompany(request);

        Project project= Project.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endingDate(request.getEndingDate())
                .description(request.getDescription())
                .pmPhone(request.getPmPhone())
                .pmEmail(request.getPmEmail())
                .pmName(request.getPmName())
                .implementorName(request.getImplementorName())
                .implementorEmail(request.getImplementorEmail())
                .implementorPhone(request.getImplementorPhone())
                .company(company)
                .numberOfUpdateEndDate(0).build();

       return projectRepo.save(project);
    }



//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Project addCompanyToProject(String companyName, String projectName) {
        Project project = findProjectByName(projectName);
        Company company=companyService.findByName(companyName);
       project.setCompany(company);

        return projectRepo.save(project);
    }


//----------------------------------------------------------------------------------------------------------------------

    @Transactional
    @Override
    public void deleteById(Long id){
        projectRepo.deleteById(id);
    }

//----------------------------------------------------------------------------------------------------------------------

    @Transactional
    @Override
    public void deleteByName(String name){
        projectRepo.findByName(name).orElseThrow(() ->new NoProjectsFoundException("There is no Project with name "+name));
        projectRepo.deleteByName(name);
    }


//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void delete(){
      List<Project>projects = projectRepo.findAll();
      if (projects.isEmpty()){
          throw new ResourceNotFoundException("There is no Projects To be Deleted");
      }
        projectRepo.deleteAll();
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Project updateProject(UpdateProjectRequest request){
        Project project=projectRepo.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + request.getId()));

        CheckIsProjectExistWithDifferentName(request);
        int numberOfUpdateEndingDate=calcNumberOfChangeEndDate(request,project);

        Company ProjectCompany=checkForProjectCompany(request,project);

        if (request.getStartDate().isAfter(request.getEndingDate())){
            throw new InvalidInputException("Start date must be before end date");
        }


        System.out.println("number of update =" +numberOfUpdateEndingDate);
        project.setName(request.getName());
        project.setStartDate(request.getStartDate());
        project.setEndingDate(request.getEndingDate());
        project.setDescription(request.getDescription());
        project.setPmPhone(request.getPmPhone());
        project.setPmEmail(request.getPmEmail());
        project.setPmName(request.getPmName());
        project.setImplementorName(request.getImplementorName());
        project.setImplementorEmail(request.getImplementorEmail());
        project.setImplementorPhone(request.getImplementorPhone());
        project.setNumberOfUpdateEndDate(numberOfUpdateEndingDate);
        project.setCompany(ProjectCompany);


        return projectRepo.save(project);

    }

//---------------------------------------------------------------------------------------------------------------------

    @Override
    public void CheckIsProjectExistWithDifferentName(UpdateProjectRequest request){
        Optional<Project> project =findProjectByNameOrNull(request.getName());

        if (project.isPresent() && !request.getId().equals(project.get().getId())) {
                throw new RuntimeException("There is already project with this name ," +
                        " you can modify it by use id --> " + project.get().getId());
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public int calcNumberOfChangeEndDate(UpdateProjectRequest request, Project project){
        int numberOfUpdateEndingDate=project.getNumberOfUpdateEndDate();

        LocalDate requestEndDate=request.getEndingDate();
        LocalDate currentEndDate= project.getEndingDate();

        if(!requestEndDate.equals(currentEndDate)){numberOfUpdateEndingDate= numberOfUpdateEndingDate+1;}
        return numberOfUpdateEndingDate;
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Company checkForProjectCompany(UpdateProjectRequest request, Project project){
        if (request.getCompanyId()!=null){
            return companyService.findById(request.getCompanyId());
        }
        return project.getCompany();
    }


//----------------------------------------------------------------------------------------------------------------------

    @Override
    public List<Project> getAllExpiredProjects(){
        return projectRepo.findExpiredProjects();
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public long getNumberOfExpiredProjects(){
        return projectRepo.countExpiredProjects();
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public long getNumberOfActiveProjects(){
        return projectRepo.countActiveProjects();
    }


//----------------------------------------------------------------------------------------------------------------------

    public ProjectDTO ConvertProjectToProjectDto(Project project) {
        return modelMapper.map(project, ProjectDTO.class);
    }

//---------------------------------------------------------------------------------------------------------------------------------

     //helper Method To Valid Request [Date and Existing ]
    private void ValidateRequestDateAndExisting(ProjectRequest request) {
        if (request.getStartDate().isAfter(request.getEndingDate())){
            throw new InvalidInputException("Start date must be before end date");
        } else if (findProjectByNameOrNull(request.getName()).isPresent()) {
            throw new ResourceAlreadyExistException(request.getName()+" Already Added");
        }
    }

//----------------------------------------------------------------------------------------------------------------------------------------------

    //Helper Method For return Project company
    private Company getCompany(ProjectRequest request) {
        Company company;
        if(request.getCompanyName() != null){
            company=companyService.findByName(request.getCompanyName());
        }else {
            company=null;
        }
        return company;
    }
}
