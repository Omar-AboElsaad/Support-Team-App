package com.Support.SupportTeam.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class  updateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private long phone;
}
