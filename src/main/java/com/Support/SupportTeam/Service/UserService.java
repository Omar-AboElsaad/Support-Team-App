package com.Support.SupportTeam.Service;

import com.Support.SupportTeam.CustomExceptions.ResourceAlreadyExistException;
import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.UserDTO;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.Role;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Repository.RolesRepo;
import com.Support.SupportTeam.Repository.UserRepo;
import com.Support.SupportTeam.Request.createUserRequest;
import com.Support.SupportTeam.Request.updateUserRequest;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


    @RequiredArgsConstructor
    @Service
public class UserService  {
        private final UserRepo userRepo;
        private final ModelMapper modelMapper;
        private final PasswordEncoder   passwordEncoder;
        private final RolesRepo rolesRepo;
        private static final Logger logger = LoggerFactory.getLogger(UserService.class);

//-------------------------------------------------------------------------------------------------

        public User getAuthenticatedUser() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            return userRepo.findByemail(email);
        }

//-------------------------------------------------------------------------------------------------

        public List<User> getAllUsers() {
            List<User> users= userRepo.findAll();
            if(users.isEmpty())throw new ResourceNotFoundException("There is no Users");
            return users;
        }

//-------------------------------------------------------------------------------------------------

        public List<User> getAllNotActiveUsers() {
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
            return userRepo.findByemail(email);
        }


//-------------------------------------------------------------------------------------------------

        public List<User> findByRole(String Role){
            return userRepo.findByRoles_Name(Role);
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
                 throw new ResourceNotFoundException("There is no User support "+project);
             return users;
        }

//-------------------------------------------------------------------------------------------------

        public User createUser(createUserRequest request, String Role) {
            Optional<Role> UserRole=rolesRepo.findByName(Role);
            boolean isActive= Role.equals("ROLE_MANAGER");

            if (userRepo.existsByemail((request.getEmail()))) {
                throw new ResourceAlreadyExistException(request.getEmail() + " Already Exist!");
            } else {
                User user = new User();
                Role NewUserRole=UserRole.orElseThrow(() -> new ResourceNotFoundException("There is no Role called "+Role));
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setActive(isActive);
                user.setRoles(Set.of(NewUserRole));
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                return userRepo.save(user);
            }


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
        public void removeUser(Long userId) {
            User user=getUserById(userId);
            System.out.println(user.getFirstName());
            user.setActive(false);
            userRepo.save(user);
            userRepo.flush();
        }

//-------------------------------------------------------------------------------------------------

        public User activeUser(Long userId) {
            return userRepo.findById(userId).map(existingMember->
            {
                existingMember.setActive(true);
                return userRepo.save(existingMember);
            }).orElseThrow(() ->
                    new ResourceNotFoundException("There is no user with Id " + userId));

        }

//----------------------------------------------------------------------------------------------------------------------

      public void assignUserToProject(User user, Project project) {
          Set<Project>userProjects=user.getProjects();
          userProjects.add(project);

          user.setProjects(userProjects);
          userRepo.save(user);

      }

//----------------------------------------------------------------------------------------------------------------------

        public void RemoveUserFromProject(User user, Project project) {
            Set<Project>userProjects=user.getProjects();
            Set<Project>newUserProjects=new HashSet<>();
            userProjects.forEach(userProject->{
               if (!userProject.getName().equals(project.getName()))
                   newUserProjects.add(userProject);
           });

            user.setProjects(newUserProjects);
            userRepo.save(user);

        }















//-------------------------------------------------------------------------------------------------

        public UserDTO ConvertUserToUserDto(User user) {
            return modelMapper.map(user, UserDTO.class);
        }
}

