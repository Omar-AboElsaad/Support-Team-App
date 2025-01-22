package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.updateUserRequest;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.*;

@AllArgsConstructor
@RestController
@RequestMapping("/current-login")
public class SupportMemberController {
    private final UserService userService;




    @PutMapping("/update-account")
    public ResponseEntity<ApiResponse> updateInfo(@RequestBody updateUserRequest request) {
        try {
            User user=userService.getAuthenticatedUser();
            User updatedUser=userService.updateUser(request,user.getId());
            UserDTO userDto=userService.ConvertUserToUserDto(updatedUser);
            return ResponseEntity.ok()
                    .body(new ApiResponse("User Updated Successfully!",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/view-profile")
    public ResponseEntity<ApiResponse> viewProfile(){
        try {
            User user=userService.getAuthenticatedUser();
            return ResponseEntity.status(FOUND).body(new ApiResponse("Founded!",user));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/view-projects")
    public ResponseEntity<ApiResponse> viewProjects(){
        try {
            User user=userService.getAuthenticatedUser();
            return ResponseEntity.status(FOUND).body(new ApiResponse("Founded!",user.getProjects()));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }
}
