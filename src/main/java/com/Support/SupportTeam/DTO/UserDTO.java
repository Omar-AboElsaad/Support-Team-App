package com.Support.SupportTeam.DTO;

import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.Role;
import lombok.Data;
import java.util.Collection;
import java.util.Set;

@Data
public class UserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private long phone;
    private boolean active;
    private Collection<Role> roles;


}
