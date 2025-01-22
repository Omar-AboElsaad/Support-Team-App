package com.Support.SupportTeam.Request;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectRequest {

    @NotBlank(message = "name can not be empty")
    private String name;
    @NotNull(message = "start can not be null")
    private Date startDate;
    @NotNull(message = "endingDate can not be null")
    private Date endingDate;
}
