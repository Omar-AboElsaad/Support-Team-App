package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.LoginRequest;
import com.Support.SupportTeam.Request.createUserRequest;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Response.JwtResponse;
import com.Support.SupportTeam.Security.JWT.JwtUtiles;
import com.Support.SupportTeam.Security.User.SupportUserDetails;
import com.Support.SupportTeam.Service.EmailService;
import com.Support.SupportTeam.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@AllArgsConstructor
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtiles jwtUtiles;
    private final UserService userService;
    private final EmailService emailService;



    @PostMapping("/login")
    public ResponseEntity<ApiResponse>Login(@Valid @RequestBody LoginRequest request){
        try {

            Authentication authentication=authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken
                            (request.getEmail(),request.getPassword()));

            User user=userService.getUserByEmail(authentication.getName());
            if (!user.isActive()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Your account is not approved yet", null));

            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt=jwtUtiles.generateTokenForUser(authentication);
            SupportUserDetails userDetails=(SupportUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse=new JwtResponse(userDetails.getId(),jwt);

            return ResponseEntity.ok().body(new ApiResponse("Login Successfully",jwtResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        }
    }

//-------------------------------------------------------------------------------------------------------------------------
    //These mains that only managers can create new manager user
    @PostMapping("/register-as-manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> RegisterAsManager(@Valid @RequestBody createUserRequest request) {
        try {
            String ManagerMailBody="Dear Eng. "+ request.getFirstName()+" ,\n Thank you for being part of NTG company. We would like to inform you that you now have an account on the Support App. \n Below are the details of your account: \n "+request.getEmail()+"\n"+request.getPassword();

            User user=userService.createUser(request,"ROLE_MANAGER");
            //convert user to user DTO
            UserDTO userDto=userService.ConvertUserToUserDto(user);


            emailService.sendEmail(request.getEmail()
                    ,"Register",
                    ManagerMailBody);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Registered successfully!",userDto));

        }catch (Exception e){
            return ResponseEntity.status(NOT_ACCEPTABLE)
                    .body(new ApiResponse(e.getMessage(),null));
        }

    }

//-------------------------------------------------------------------------------------------------

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createSupportUser(@RequestBody createUserRequest request) {
        try {
            User user=userService.createUser(request,"ROLE_SUPPORT-MEMBER");
            //Get All Manager Users
            List<User> Managers=userService.findByRole("ROLE_MANAGER");
            //convert user to user DTO
            UserDTO userDto=userService.ConvertUserToUserDto(user);
            emailService.sendEmail(
                    request.getEmail(),
                    "Registration",
                    "Register Successfully , Please wait until Manager active your account");

            //Send Mail To All Managers To let them know that there is a new member register and need to activate his account
            Managers.forEach(manager ->{
                emailService.sendEmail(manager.getEmail(),
                    "Support Member Account Approve",
                    "Hello Eng. " +manager.getFirstName()+" , \n"+
                            request.getFirstName()+" need To Activate his Account ");
            });


            return ResponseEntity.ok()
                    .body(new ApiResponse("Register Successfully , Please wait until Manager activate your account",userDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(),null));
        }

    }


}
