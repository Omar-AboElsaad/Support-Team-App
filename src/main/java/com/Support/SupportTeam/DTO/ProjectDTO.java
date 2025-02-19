package com.Support.SupportTeam.DTO;

import com.Support.SupportTeam.Entity.User;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
public class ProjectDTO {
    private Long id;
    private String name;
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;
    @Temporal(TemporalType.DATE)
    private LocalDate endingDate;
    private String description;
    private String pmName;
    private String pmEmail;
    private String pmPhone;
    private String implementorName;
    private String implementorEmail;
    private String implementorPhone;
    private int numberOfUpdateEndDate;
    private String companyName;
    private Set<UserDTO> supportMembers ;


}
