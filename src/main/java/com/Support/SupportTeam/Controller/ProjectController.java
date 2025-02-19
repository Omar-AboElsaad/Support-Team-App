package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.DTO.ProjectDTO;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.ProjectRequest;
import com.Support.SupportTeam.Request.UpdateProjectRequest;
import com.Support.SupportTeam.Response.ApiPaganationResponse;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Service.Project.ProjectService;
import com.Support.SupportTeam.Service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

//-------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-all-projects")
    public  ResponseEntity<ApiPaganationResponse> findAllProjects(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int pageSize){

            Page<Project> projects= projectService.findAllProject(page , pageSize);
            List<ProjectDTO> projectDTOS=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
            return ResponseEntity.status(OK).body(new ApiPaganationResponse("Projects Founded",projectDTOS,projects.getTotalElements(),
                    projects.getTotalPages()));

    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-name")
    public  ResponseEntity<ApiResponse> findProjectByName(@RequestParam String projectName){

            Project project= projectService.findProjectByName(projectName);
            ProjectDTO projectDTO=projectService.ConvertProjectToProjectDto(project);
            return ResponseEntity.status(OK).body(new ApiResponse("Project Founded",projectDTO));

    }

//-------------------------------------------------------------------------------------------------------------------------------------
    //This method allow us to search by each character input and return all matchers with any character written
    @GetMapping("/search-by-name")
    public  ResponseEntity<ApiResponse> searchForProjectByName(@RequestParam String projectName){

        List<Project> projects= projectService.SearchProjectByName(projectName);
        List<ProjectDTO> projectDTOs=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
        return ResponseEntity.status(OK).body(new ApiResponse("Project Founded",projectDTOs));

    }

//-------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-id")
    public  ResponseEntity<ApiResponse> findProjectByID(@RequestParam Long id){

            Project project= projectService.findById(id);
            ProjectDTO projectDTO=projectService.ConvertProjectToProjectDto(project);
            return ResponseEntity.status(OK).body(new ApiResponse("Project Founded",projectDTO));

    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-starting-date")
    public  ResponseEntity<ApiResponse> findProjectByStartingDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startingDate){

            List<Project> projects= projectService.findProjectByStartingDate(startingDate);
            List<ProjectDTO> projectDTOS=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
            return ResponseEntity.status(OK).body(new ApiResponse("Projects Founded",projectDTOS));

    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-ending-date")
    public  ResponseEntity<ApiResponse> findProjectByEndingDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endingDate){

            List<Project> projects= projectService.findProjectByEndingDate(endingDate);
            List<ProjectDTO> projectDTOS=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
            return ResponseEntity.status(OK).body(new ApiResponse("Projects Founded",projectDTOS));
    }


//-------------------------------------------------------------------------------------------------------------------------------------


    @GetMapping("/get-all-user-projects")
    public  ResponseEntity<ApiResponse> findProjectByUserMail(@RequestParam String mail){

            User user=userService.getUserByEmail(mail);
            List<Project> projects= projectService.findAllUserProject(user);
            List<ProjectDTO> projectDTOS=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
            return ResponseEntity.status(OK).body(new ApiResponse("Projects Founded",projectDTOS));
    }


//-------------------------------------------------------------------------------------------------------------------------------------

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProject(@Valid @RequestBody ProjectRequest request){

           Project project= projectService.addProject(request);
            ProjectDTO projectDTO=projectService.ConvertProjectToProjectDto(project);
            return ResponseEntity.status(CREATED).body(new ApiResponse("Project Added Successfully",projectDTO));
    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> UpdateProject(@Valid @RequestBody UpdateProjectRequest request){

            Project project=projectService.updateProject(request);
            ProjectDTO projectDTO=projectService.ConvertProjectToProjectDto(project);
            return ResponseEntity.status(OK).body(new ApiResponse("Project Updated Successfully",projectDTO));

    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<ApiResponse> deleteByID(@PathVariable Long id){

            projectService.deleteById(id);
            return ResponseEntity.status(OK).body(new ApiResponse("Project Deleted Successfully",null));

    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/delete-by-name")
    public ResponseEntity<ApiResponse> deleteByName(@RequestParam String projectName){

        projectService.deleteByName(projectName);
        return ResponseEntity.status(OK).body(new ApiResponse("Project Deleted Successfully",null));

    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/delete-all")
    public ResponseEntity<ApiResponse> deleteAll(){

            projectService.delete();
            return ResponseEntity.status(OK).body(new ApiResponse("All Projects Deleted Successfully",null));

    }

//-------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/expired")
    public ResponseEntity<ApiResponse> getAllExpiredProjects(){

            List<Project> projects= projectService.getAllExpiredProjects();
            return ResponseEntity.status(OK).body(new ApiResponse("Expired Projects Founded",projects));

    }

//-------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/number-expired")
    public ResponseEntity<ApiResponse> getNumberOfExpiredProjects(){
        long numOfProject= projectService.getNumberOfExpiredProjects();
        if (numOfProject == 0) {
            return ResponseEntity.status(NO_CONTENT).build();
        }
        return ResponseEntity.status(OK).body(new ApiResponse("Number Of Expired Projects Founded",numOfProject));
    }

//-------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/number-active")
    public ResponseEntity<ApiResponse> getNumberOfActiveProjects(){
        long numOfProject= projectService.getNumberOfActiveProjects();
        if (numOfProject == 0) {
            return ResponseEntity.status(NO_CONTENT).build();
        }
        return ResponseEntity.status(OK).body(new ApiResponse("Number Of Active Projects Founded",numOfProject));
    }



}
