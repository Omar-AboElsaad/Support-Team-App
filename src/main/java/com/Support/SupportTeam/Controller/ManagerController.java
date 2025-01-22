package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.Role;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.createUserRequest;
import com.Support.SupportTeam.Request.updateUserRequest;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Service.EmailService;
import com.Support.SupportTeam.Service.ProjectService;
import com.Support.SupportTeam.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@AllArgsConstructor
@RestController
@RequestMapping("/manager")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ManagerController {
    private final UserService userService;
    private final EmailService emailService;
    private final ProjectService projectService;


//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-All-users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<User> users=userService.getAllUsers();
            List<UserDTO> userDto=users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-All-inactive-users")
    public ResponseEntity<ApiResponse> getAllInactiveUsers() {
        try {
            List<User> users=userService.getAllNotActiveUsers();
            List<UserDTO> userDto=users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-id")
    public ResponseEntity<ApiResponse> getUserById(@RequestParam Long userId) {
        try {
            User user=userService.getUserById(userId);
            UserDTO userDto=userService.ConvertUserToUserDto(user);

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }


//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-name")
    public ResponseEntity<ApiResponse> getUserByName(@RequestParam String name) {
        try {
            List<User> users=userService.findByName(name);
            List<UserDTO> userDto=users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }


//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-mail")
    public ResponseEntity<ApiResponse> getUserByMail(@RequestParam String mail) {
        try {
            User user=userService.getUserByEmail(mail);
            UserDTO userDto=userService.ConvertUserToUserDto(user);

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }


//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-role")
    public ResponseEntity<ApiResponse> getUserByRole(@RequestParam String Role) {
        try {
            List<User> users=userService.findByRole(Role);
            List<UserDTO> userDTOS= users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDTOS));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-project")
    public ResponseEntity<ApiResponse> getUserByProject(@RequestParam String projectName) {
        try {
            List<User> users=userService.findByProject(projectName);
            List<UserDTO> userDTOS= users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDTOS));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }


//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/update-user")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody updateUserRequest request, @RequestParam Long userId) {
        try {
            User user=userService.updateUser(request,userId);
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            return ResponseEntity.ok()
                    .body(new ApiResponse("User Updated Successfully!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//-------------------------------------------------------------------------------------------------

    @PutMapping("/delete-by-id")
    public ResponseEntity<ApiResponse> removeUser(@RequestParam Long userId) {
        try {
            System.out.println(userId);
            userService.removeUser(userId);
            return ResponseEntity.ok()
                    .body(new ApiResponse("User Deleted !",null));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//-------------------------------------------------------------------------------------------------

    @PutMapping("/active-user")
    public ResponseEntity<ApiResponse> activeUser(@RequestParam Long userId) {
        try {
            User user=userService.activeUser(userId);
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            emailService.sendEmail(user.getEmail(),"Account Activation",
                     "Dear "+user.getFirstName()+ " \nWe would like to inform you that your account is active and you can login now ");

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Active Successfully!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//-------------------------------------------------------------------------------------------------

    @PutMapping("/active-all-user")
    public ResponseEntity<ApiResponse> activeAllInactiveUsers() {
        try {
            List<User> InactiveUsers=userService.getAllNotActiveUsers();
            List<User> activeUsers=InactiveUsers.stream().map(user ->userService.activeUser(user.getId()) ).toList();
            List<UserDTO> ActiveUserDto=activeUsers.stream().map(userService::ConvertUserToUserDto).toList();
            activeUsers.forEach(user -> emailService.sendEmail(
                    user.getEmail()
                    ,"Account Activation"
                    , "Dear "+user.getFirstName()+ " \nWe would like to inform you that your account is active and you can login now ")
);
            return ResponseEntity.ok()
                    .body(new ApiResponse("All Users Active now",ActiveUserDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/assign-project-to-user")
    public ResponseEntity<ApiResponse> assignProject(@RequestParam String email,@RequestParam String projectName) {
        try {
            User user=userService.getUserByEmail(email);
            Project project=projectService.findProjectByName(projectName);
            userService.assignUserToProject(user,project);
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            emailService.sendEmail(user.getEmail(),"Assigned Project",
                    "Dear "+user.getFirstName()+" \nWe would like to inform you that your Manager Assigned you to project "+projectName);

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Assigned Successfully!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/remove-user-from-project")
    public ResponseEntity<ApiResponse> removeUserFromProject(@RequestParam String email,@RequestParam String projectName) {
        try {
            User user=userService.getUserByEmail(email);
            Project project=projectService.findProjectByName(projectName);
            userService.RemoveUserFromProject(user,project);
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            emailService.sendEmail(user.getEmail(),"Remove From Project",
                    "Dear "+user.getFirstName()+" \nWe would like to inform you that your Manager \bRemove you from project "+projectName);

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Removed Successfully!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

//----------------------------------------------------------------------------------------------------------------------



}
