package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.updateUserRequest;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Service.EmailService;
import com.Support.SupportTeam.Service.Project.ProjectService;
import com.Support.SupportTeam.Service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController

@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ManagerController {
    private final UserService userService;
    private final EmailService emailService;
    private final ProjectService projectService;


//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-All-users")
    public ResponseEntity<ApiResponse> getAllUsers() {

            List<User> users=userService.getAllUsers();
            List<UserDTO> userDto=users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.status(OK)
                    .body(new ApiResponse("User Found!",userDto));

    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-All-inactive-users")
    public ResponseEntity<ApiResponse> getAllInactiveUsers() {

            List<User> users=userService.getAllInactiveUsers();
            List<UserDTO> userDto=users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.status(OK)
                    .body(new ApiResponse("User Found!",userDto));

    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-id")
    public ResponseEntity<ApiResponse> getUserById(@RequestParam Long userId) {

            User user=userService.getUserById(userId);
            UserDTO userDto=userService.ConvertUserToUserDto(user);

            return ResponseEntity.status(OK)
                    .body(new ApiResponse("User Found!",userDto));

    }


//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-name")
    public ResponseEntity<ApiResponse> getUserByName(@RequestParam String name) {

            List<User> users=userService.findByName(name);
            List<UserDTO> userDto=users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDto));
    }


//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-mail")
    public ResponseEntity<ApiResponse> getUserByMail(@RequestParam String mail) {

            User user=userService.getUserByEmail(mail);
            UserDTO userDto=userService.ConvertUserToUserDto(user);

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDto));
    }


//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-role")
    public ResponseEntity<ApiResponse> getUserByRole(@RequestParam String Role) {

            List<User> users=userService.findByRole(Role);
            List<UserDTO> userDTOS= users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDTOS));
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-project")
    public ResponseEntity<ApiResponse> getUserByProject(@RequestParam String projectName) {

            List<User> users=userService.findByProject(projectName);
            List<UserDTO> userDTOS= users.stream().map(userService::ConvertUserToUserDto).toList();

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Found!",userDTOS));
    }


//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/update-user")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody updateUserRequest request, @RequestParam Long userId) {

            User user=userService.updateUser(request,userId);
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            return ResponseEntity.ok()
                    .body(new ApiResponse("User Updated Successfully!",userDto));
    }

//-------------------------------------------------------------------------------------------------

    @DeleteMapping("/delete-by-id")
    public ResponseEntity<ApiResponse> removeUser(@RequestParam Long userId) {

            userService.removeUserById(userId);
            return ResponseEntity.ok()
                    .body(new ApiResponse("User Deleted !",null));

    }

    //-------------------------------------------------------------------------------------------------

    @DeleteMapping("/delete-by-mail")
    public ResponseEntity<ApiResponse> deleteUser(@RequestParam String mail) {

            userService.deleteUserByMail(mail);
            return ResponseEntity.ok()
                    .body(new ApiResponse("User Deleted !",null));

    }

//-------------------------------------------------------------------------------------------------

    @PutMapping("/active-user")
    public ResponseEntity<ApiResponse> activeUser(@RequestParam Long userId) throws MessagingException, IOException {

            User user=userService.activeUser(userId);
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            NotifyAllInactiveUsers(List.of(user));

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Active Successfully!",userDto));

    }

//-------------------------------------------------------------------------------------------------

    @PutMapping("/active-all-user")
    public ResponseEntity<ApiResponse> activeAllInactiveUsers() {
       
            
            List<User> activeUsers=userService.activeAllInactiveUsers();
            List<UserDTO> ActiveUserDto=activeUsers.stream().map(userService::ConvertUserToUserDto).toList();

            //Notify All Inactive users with the Activation
            NotifyAllInactiveUsers(activeUsers);

        return ResponseEntity.ok()
                    .body(new ApiResponse("All Users Active now",ActiveUserDto));

    }

//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/assign-projects-to-user")
    public ResponseEntity<ApiResponse> assignProjects(@RequestParam String email,@RequestBody List<String> projects) throws MessagingException, IOException {

            User user=userService.getUserByEmail(email);
            List<Project> project = projects.stream()
                    .map(projectService::findProjectByName).toList();

            userService.assignUserToProject(user,project);
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            emailService.sendEmail(user.getEmail(),"Assigned Project",
                    "Dear "+user.getFirstName()+" \n" +
                            "We would like to inform you that your Manager Assigned you to projects "+projects);

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Assigned Successfully!",userDto));

    }

//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/remove-user-from-project")
    public ResponseEntity<ApiResponse> removeUserFromProject(@RequestParam String email,@RequestBody List<String> projects) throws MessagingException, IOException {
      
            User user=userService.getUserByEmail(email);
            List<Project> project=projects.stream()
                    .map(projectService::findProjectByName).toList();

            userService.RemoveUserFromProject(user,project);
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            emailService.sendEmail(user.getEmail(),"Remove From Project",
                    "Dear "+user.getFirstName()+" \nWe would like to inform you that your Manager \bRemove you from project "+projects);

            return ResponseEntity.ok()
                    .body(new ApiResponse("User Removed Successfully!",userDto));
        
    }

//----------------------------------------------------------------------------------------------------------------------

    //Helper Method To send mail for inactive users to inform them that their accounts is activated
    private void NotifyAllInactiveUsers(List<User> activeUsers) {
        activeUsers.forEach(user -> {

            try {
                emailService.sendEmail(user.getEmail(), "Account Activation"
                        , "Dear " + user.getFirstName() + " ,\n" +
                                "We would like to inform you that your account is active and you can login now "
                );
            } catch (IOException | MessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
