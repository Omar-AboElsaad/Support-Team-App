package com.Support.SupportTeam.Security.User;

import lombok.AllArgsConstructor;
import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class SupportUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
     User user= Optional.ofNullable(userRepo.findByemail(email)).
             orElseThrow(()->new ResourceNotFoundException("User with email "+email+" Not Found"));

        return SupportUserDetails.buildUserDetails(user);
    }
}
