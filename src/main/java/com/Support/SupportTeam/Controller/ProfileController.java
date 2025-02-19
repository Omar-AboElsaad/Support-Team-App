package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.ChangePasswordDTO;
import com.Support.SupportTeam.Request.updateUserRequest;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.management.BadAttributeValueExpException;
import static org.springframework.http.HttpStatus.*;


@AllArgsConstructor
@RestController
public class ProfileController {
    private final UserService userService;

    @GetMapping("/view-profile")
    public ResponseEntity<ApiResponse> viewProfile(){
        System.out.println("i am in view profile ");
        try {
            User user=userService.getAuthenticatedUser();
            System.out.println(user);
            return ResponseEntity.status(OK).body(new ApiResponse("Founded!",user));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){
        try {
            userService.changePassword(changePasswordDTO);
            return ResponseEntity.status(OK).body(new ApiResponse("Password updated successfully.",null));
        } catch (BadAttributeValueExpException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
  }

//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/update-account")
    public ResponseEntity<ApiResponse> updateInfo(@RequestBody updateUserRequest request) {
        try {
            System.out.println("i am in update account");
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

}
