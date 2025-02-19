package com.Support.SupportTeam.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ProjectRequest {

    @NotBlank(message = "name can not be empty")
    private String name;

    @NotNull(message = "start can not be null")
    private LocalDate startDate;

    @NotNull(message = "endingDate can not be null")
    private LocalDate endingDate;

    @NotNull(message = "description can not be null")
    private String description;

    @NotNull(message = "pmName can not be null")
    private String pmName;

    @NotNull(message = "pmEmail can not be null")
    @Email(message = "Invalid Email")
    private String pmEmail;

    @NotNull(message = "pmPhone can not be null")
    private String pmPhone;

    @NotNull(message = "implementorName can not be null")
    private String implementorName;

    @NotNull(message = "implementorEmail can not be null")
    private String implementorEmail;

    @NotNull(message = "implementorPhone can not be null")
    private String implementorPhone;

    private String companyName;

}
