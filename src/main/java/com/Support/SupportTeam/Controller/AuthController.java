package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.CustomExceptions.ResourceAlreadyExistException;
import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.ForgetPasswordDTO;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Request.ChangePasswordDTO;
import com.Support.SupportTeam.Request.LoginRequest;
import com.Support.SupportTeam.Request.createUserRequest;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Response.JwtResponse;
import com.Support.SupportTeam.Security.JWT.JwtUtiles;
import com.Support.SupportTeam.Security.User.SupportUserDetails;
import com.Support.SupportTeam.Service.EmailService;
import com.Support.SupportTeam.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

import javax.management.BadAttributeValueExpException;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtiles jwtUtiles;
    private final UserService userService;
    private final EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse>Login(@Valid @RequestBody LoginRequest request){

            System.out.println("iam here in login");
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

    }

//-------------------------------------------------------------------------------------------------------------------------

    //These mains that only managers can create new manager user
    @PostMapping("/add-manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> CreateManagerAccount(@Valid @RequestBody createUserRequest request) throws MessagingException, IOException {


            String ManagerMailBody=emailService.loadRegisterManagerEmailTemplate().replace("${username}", request.getFirstName());

            // Create the Manager account with a default role
            User user=userService.createUser(request,"ROLE_MANAGER");

            //convert user to user DTO
            UserDTO userDto=userService.ConvertUserToUserDto(user);

            // Notify managers about the successful registration
            emailService.sendEmail(request.getEmail()
                    ,"Register",
                    ManagerMailBody);

            return ResponseEntity.status(OK)
                    .body(new ApiResponse("Registered successfully!",userDto));

    }

//-------------------------------------------------------------------------------------------------

    //Register new support user
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createSupportUser( @RequestBody createUserRequest request) throws IOException, MessagingException {

            System.out.println("i am in register");

            // Create the user with a default role
            User user=userService.createUser(request,"ROLE_SUPPORT-MEMBER");

            //convert user to user DTO
            UserDTO userDto=userService.ConvertUserToUserDto(user);

            // Load email template and replace placeholder
            String userMailBody=emailService.loadSupportUserEmailTemplate().replace("${username}", request.getFirstName());


            // Notify new user about the registration successfully
            emailService.sendEmail(request.getEmail(), "Registration", userMailBody);


        //Get All Manager Users
            List<User> Managers=userService.findByRole("ROLE_MANAGER");

            // Notify all managers about the new user and ask for activation
            Managers.forEach(manager ->{
                try {
                    // Load email template and replace placeholder
                    String ManagerMailBody=emailService.loadManagerEmailTemplate()
                            .replace("${managerName}", manager.getFirstName())
                            .replace("${UserName}",request.getFirstName())
                            .replace("${userId}",String.valueOf(user.getId()));

                    emailService.sendEmail(manager.getEmail(),
                        "Support Member Account Approve",
                        ManagerMailBody);
                } catch (IOException | MessagingException e) {
                    throw new RuntimeException(e);
                }
            });


            return ResponseEntity.status(OK)
                    .body(new ApiResponse("Register Successfully , Please wait until Manager activate your account",userDto));

    }

//------------------------------------------ Forget Password Methods ------------------------------------------------------------------------------------------------------------------

    @PostMapping("/generate-otp")
    public ResponseEntity<ApiResponse> generateOTP(@Valid @NotNull @RequestParam String email) {
        try {

            String otp=userService.generateOtp(6);

            User user=userService.getUserByEmail(email);

            userService.sendAndStoreOtp(otp,user);

            return ResponseEntity.status(OK).body(new ApiResponse("Otp Send Successfully",null));
        }catch (Exception e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/valid-otp")
    public ResponseEntity<ApiResponse> ValidateOTP(@RequestParam String mail,String otp) {
        User user=userService.getUserByEmail(mail);
       boolean isValid= userService.otpIsValid(otp,user);
       return ResponseEntity.status(OK).body(new ApiResponse("This is Validation result",isValid));
    }

//------------------------------------------------------------------------------------------------------------------------------------------------------------

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse> forgetPassword(@RequestBody ForgetPasswordDTO forgetPasswordDTO){
        try {
            System.out.println(forgetPasswordDTO);
            userService.forgetPassword(forgetPasswordDTO);
            return ResponseEntity.status(OK).body(new ApiResponse("Password updated successfully.",null));
        } catch (BadAttributeValueExpException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }


}
