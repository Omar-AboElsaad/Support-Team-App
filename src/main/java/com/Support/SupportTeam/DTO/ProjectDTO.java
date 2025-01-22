package com.Support.SupportTeam.DTO;

import com.Support.SupportTeam.Entity.User;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
public class ProjectDTO {

    private String name;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    private Date endingDate;
    private Set<UserDTO> SupportMembers ;


}
