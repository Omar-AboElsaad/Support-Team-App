package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.DTO.ProjectDTO;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.ProjectRequest;
import com.Support.SupportTeam.Request.UpdateProjectRequest;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Service.ProjectService;
import com.Support.SupportTeam.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@AllArgsConstructor
@RestController
@RequestMapping("/project")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping("/get-all-projects")
    public  ResponseEntity<ApiResponse> findAllProjects(){
        try {
            List<Project> projects= projectService.findAllProject();
            List<ProjectDTO> projectDTOS=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
            return ResponseEntity.status(FOUND).body(new ApiResponse("Projects Founded",projectDTOS));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));

        }
    }


//-------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-name")
    public  ResponseEntity<ApiResponse> findProjectByName(@RequestParam String projectName){
        try {
            Project project= projectService.findProjectByName(projectName);
            ProjectDTO projectDTO=projectService.ConvertProjectToProjectDto(project);
            return ResponseEntity.status(FOUND).body(new ApiResponse("Project Founded",projectDTO));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));

        }
    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-starting-date")
    public  ResponseEntity<ApiResponse> findProjectByStartingDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startingDate){
        try {
            List<Project> projects= projectService.findProjectByStartingDate(startingDate);
            List<ProjectDTO> projectDTOS=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
            return ResponseEntity.status(FOUND).body(new ApiResponse("Projects Founded",projectDTOS));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));


        }
    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-ending-date")
    public  ResponseEntity<ApiResponse> findProjectByEndingDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endingDate){
        try {
            List<Project> projects= projectService.findProjectByEndingDate(endingDate);
            List<ProjectDTO> projectDTOS=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
            return ResponseEntity.status(FOUND).body(new ApiResponse("Projects Founded",projectDTOS));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));


        }
    }


//-------------------------------------------------------------------------------------------------------------------------------------


    @GetMapping("/get-all-user-projects")
    public  ResponseEntity<ApiResponse> findProjectByUserMail(@RequestParam String mail){
        try {
            User user=userService.getUserByEmail(mail);
            List<Project> projects= projectService.findAllUserProject(user);
            List<ProjectDTO> projectDTOS=projects.stream().map(projectService::ConvertProjectToProjectDto).toList();
            return ResponseEntity.status(FOUND).body(new ApiResponse("Projects Founded",projectDTOS));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));


        }
    }


//-------------------------------------------------------------------------------------------------------------------------------------

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProject(@Valid @RequestBody ProjectRequest request){
        try {
           Project project= projectService.addProject(request);
            ProjectDTO projectDTO=projectService.ConvertProjectToProjectDto(project);
            return ResponseEntity.status(ACCEPTED).body(new ApiResponse("Project Added Successfully",projectDTO));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(),null));

        }
    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> UpdateProject(@Valid @RequestBody UpdateProjectRequest request){
        try {
            Project project=projectService.updateProject(request);
            ProjectDTO projectDTO=projectService.ConvertProjectToProjectDto(project);
            return ResponseEntity.status(ACCEPTED).body(new ApiResponse("Project Updated Successfully",projectDTO));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(),null));

        }
    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/delete-by-name")
    public ResponseEntity<ApiResponse> deleteByName(@RequestParam String projectName){
        try {
            projectService.deleteByName(projectName);
            return ResponseEntity.status(ACCEPTED).body(new ApiResponse("Project Deleted Successfully",null));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(),null));

        }

    }

//-------------------------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/delete-all")
    public ResponseEntity<ApiResponse> deleteAll(){
        try {
            projectService.delete();
            return ResponseEntity.status(ACCEPTED).body(new ApiResponse("All Projects Deleted Successfully",null));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));

        }

    }



}
