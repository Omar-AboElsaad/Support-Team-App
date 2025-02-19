package com.Support.SupportTeam.Service;

import com.Support.SupportTeam.CustomExceptions.ResourceAlreadyExistException;
import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.CustomExceptions.UserDeletedException;
import com.Support.SupportTeam.DTO.ForgetPasswordDTO;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.Role;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Repository.ProjectRepo;
import com.Support.SupportTeam.Repository.RolesRepo;
import com.Support.SupportTeam.Repository.UserRepo;
import com.Support.SupportTeam.Request.ChangePasswordDTO;
import com.Support.SupportTeam.Request.createUserRequest;
import com.Support.SupportTeam.Request.updateUserRequest;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.management.BadAttributeValueExpException;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class UserService  {
        private final UserRepo userRepo;
        private final ProjectRepo projectRepo;
        private final ModelMapper modelMapper;
        private final PasswordEncoder passwordEncoder;
        private final RolesRepo rolesRepo;
        private final EmailService emailService;
        private static final SecureRandom secureRandom=new SecureRandom();

//-------------------------------------------------------------------------------------------------

        public User getAuthenticatedUser() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            return userRepo.findByEmail(email);
        }

//-------------------------------------------------------------------------------------------------

        public List<User> getAllUsers() {
            List<User> users= userRepo.findAll();
            if(users.isEmpty())
                throw new ResourceNotFoundException("There is no Users");
            return users;
        }

//-------------------------------------------------------------------------------------------------

        public List<User> getAllInactiveUsers() {
            List<User> users= userRepo.findNotActiveUsers();
            if(users.isEmpty())throw new ResourceNotFoundException("There is no users Needed to activate");
            return users;
        }

//-------------------------------------------------------------------------------------------------

        public User getUserById(Long userId) {
            return userRepo.findById(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("There is no User With ID " + userId));
        }

//-------------------------------------------------------------------------------------------------

        public User getUserByEmail(String email) {
            // Trim and convert email to lowercase
            String cleanedMail = email.trim().toLowerCase();

            //If mail not contains domain then the domain will be ** @gmail.com ** by default
            if(!cleanedMail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
                cleanedMail=cleanedMail.concat("@gmail.com");
            }
            System.out.println("cleaned mail === "+cleanedMail);

            User user= userRepo.findByEmail(cleanedMail);

            if (user!=null){
                return user;
            }
            throw new ResourceNotFoundException("There is no user with email "+ email);
        }


//-------------------------------------------------------------------------------------------------

        public List<User> findByRole(String Role){
            rolesRepo.findByName(Role).orElseThrow(() -> new ResourceNotFoundException("There is no Role with name "+Role));
            List<User> users= userRepo.findByRoles_Name(Role);
            if (users.isEmpty()){
                throw new ResourceNotFoundException("There is no User with Role "+Role);
            }
            return users;
        }

//-------------------------------------------------------------------------------------------------

        public List<User> findByName(String Name){
            List<User> users= userRepo.findByFirstName(Name);
            if (users.isEmpty())throw new ResourceNotFoundException("There is no User with name "+Name);
            return users;
        }

//-------------------------------------------------------------------------------------------------

        public List<User> findByProject(String project){
            List<User>users= userRepo.findByProjects_Name(project);
             if (users.isEmpty())
                 throw new ResourceNotFoundException("There is no support user for "+project);
             return users;
        }

//-------------------------------------------------------------------------------------------------

        public User createUser(createUserRequest request, String Role) {
            Optional<Role> UserRole=rolesRepo.findByName(Role);

            //create an Active account if a role is manager
            boolean isActive= Role.equals("ROLE_MANAGER");

            if (userRepo.existsByemail(request.getEmail())) {
                throw new ResourceAlreadyExistException(request.getEmail() + " Already Exist!");
            } else {
                User user = new User();
                Role NewUserRole=UserRole.orElseThrow(() -> new ResourceNotFoundException("There is no Role called "+Role));
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setActive(isActive);
                user.setDeleted(false);
                user.setRoles(Set.of(NewUserRole));
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                return userRepo.save(user);
            }
        }

//-------------------------------------------------------------------------------------------------

    public User activeUser(Long userId) {
        return userRepo.findById(userId).map(existingMember->
        {
            existingMember.setActive(true);
            existingMember.setDeleted(false);
            return userRepo.save(existingMember);
        }).orElseThrow(() ->
                new ResourceNotFoundException("There is no user with Id " + userId));
    }

//-------------------------------------------------------------------------------------------------

    public List<User> activeAllInactiveUsers() {
        List<User> InactiveUsers=getAllInactiveUsers();
        return InactiveUsers.stream().map(user ->activeUser(user.getId()) ).toList();
        }


//-------------------------------------------------------------------------------------------------


        public User updateUser(updateUserRequest request, Long userId) {
            return userRepo.findById(userId).map(exitingUser -> {
                exitingUser.setFirstName(request.getFirstName());
                exitingUser.setLastName(request.getLastName());
                exitingUser.setPhone(request.getPhone());
                exitingUser.setEmail(request.getEmail());
                return userRepo.save(exitingUser);
            }).orElseThrow(() -> new ResourceNotFoundException("There is no user with Id " + userId));

        }


//-------------------------------------------------------------------------------------------------

        @Transactional
        public void removeUserById(Long userId) {
            User user=getUserById(userId);

            if (user.isDeleted()){
                throw new UserDeletedException("This user already Deleted");
            }
            user.setProjects(null);
            user.setActive(false);
            user.setDeleted(true);

            userRepo.save(user);

            userRepo.flush();
        }

//-------------------------------------------------------------------------------------------------

    @Transactional
    public void deleteUserByMail(String mail) {
        if ((userRepo.existsByemail(mail))) {
            User user=getUserByEmail(mail);
            removeUserById(user.getId());
        }else throw new ResourceNotFoundException("there is no user with mail "+mail);
    }

//----------------------------------------------------------------------------------------------------------------------

      public void assignUserToProject(User user, List<Project> project) {
          Set<Project>userProjects=user.getProjects();
          userProjects= project.stream().peek(userProjects::add).collect(Collectors.toSet());

          user.setProjects(userProjects);
          userRepo.save(user);

      }

//----------------------------------------------------------------------------------------------------------------------

        public void RemoveUserFromProject(User user, List<Project> projects) {
            Set<Project>userProjects=user.getProjects();

            userProjects.removeIf(userproject ->
                    projects.stream().anyMatch(targetproject ->targetproject.getName().equals(userproject.getName())));

            user.setProjects(userProjects);
            userRepo.save(user);

        }

//----------------------------------------------------------------------------------------------------------------------


        public void changePassword(ChangePasswordDTO changePasswordDTO) throws BadAttributeValueExpException {
                User user=userRepo.findByEmail(changePasswordDTO.getEmail());
            String ActualPassword=user.getPassword();
            if(passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), ActualPassword)) {
                user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
                userRepo.save(user);
            }else {
                throw new BadAttributeValueExpException("Current password is not correct");
            }

        }

//----------------------------------------------------------------------------------------------------------------------

        private static final String DIGITS = "0123456789";
        public String generateOtp(int length) {

            StringBuilder otp = new StringBuilder();
            for (int i = 0; i < length; i++) {
                otp.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
            }
            return  otp.toString();
        }

//----------------------------------------------------------------------------------------------------------------------

        public void sendAndStoreOtp(String otp,User user) throws IOException, MessagingException {

            String body= "Welcome Back "+user.getFirstName()+"\n This is your one time passcode : \n "+ otp;
            user.setOtp(otp);
            LocalDateTime expiredTime=LocalDateTime.now().plusMinutes(10);
            user.setOtpExpiry(expiredTime);
            userRepo.save(user);
            emailService.sendEmail(user.getEmail(),"Reset Password",body);
        }

//----------------------------------------------------------------------------------------------------------------------

    public void forgetPassword(ForgetPasswordDTO forgetPasswordDTO) throws BadAttributeValueExpException {
       User user=userRepo.findByEmail(forgetPasswordDTO.getEmail());
        System.out.println("i am in forget "+user.getFirstName());
        if(!otpIsValid(forgetPasswordDTO.getOtp(),user)) {
            throw new BadAttributeValueExpException("Please try again");
        }
       user.setPassword(passwordEncoder.encode(forgetPasswordDTO.getNewPassword()));
       user.setOtp(null);
       user.setOtpExpiry(null);
        System.out.println(user.getOtp());
       userRepo.save(user);
    }

//----------------------------------------------------------------------------------------------------------------------

    public boolean otpIsValid(String otp,User user){
        if (user == null || user.getOtp() == null || user.getOtpExpiry() == null) {
            return false; // User or OTP data is missing
        }

            String realOtp=user.getOtp();
            LocalDateTime currentTime=LocalDateTime.now();
            LocalDateTime otpExpireyTime=user.getOtpExpiry();

            return (currentTime.isBefore(otpExpireyTime)&&realOtp.equals(otp));

    }

//-------------------------------------------------------------------------------------------------

        public UserDTO ConvertUserToUserDto(User user) {
            return modelMapper.map(user, UserDTO.class);
        }


    }
