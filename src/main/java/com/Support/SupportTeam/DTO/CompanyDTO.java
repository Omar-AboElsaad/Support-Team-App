package com.Support.SupportTeam.DTO;

import lombok.Data;

import java.util.Set;


@Data
public class CompanyDTO {
    private String name;

    private Set<ProjectDTO> Projects;
}
