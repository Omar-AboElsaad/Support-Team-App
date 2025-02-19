package com.Support.SupportTeam.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private long phone;
    private boolean active;
    private boolean isDeleted;
    private String otp;
    private LocalDateTime otpExpiry; // Expiration time


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "Supported_Projects",
            joinColumns=@JoinColumn(name = "support_member_id",referencedColumnName = "id"),
            inverseJoinColumns =@JoinColumn(name = "project_id",referencedColumnName = "id") )
    private Set<Project> projects=new HashSet<>();
//-----------------------------------------------------------------------------------------------
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_roles",
            joinColumns=@JoinColumn(name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns =@JoinColumn(name = "role_id",referencedColumnName = "id") )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Collection<Role> roles =new HashSet<>();



}
