package com.Support.SupportTeam.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    @ManyToOne()
    @JoinColumn(name = "company_id",nullable = true)
    private Company company;


    @ManyToMany(mappedBy = "projects" , fetch = FetchType.LAZY, cascade =
            {CascadeType.MERGE,CascadeType.PERSIST})
    private Set<User> supportMembers =new HashSet<>();

}
