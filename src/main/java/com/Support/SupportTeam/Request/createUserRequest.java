package com.Support.SupportTeam.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@AllArgsConstructor
@NoArgsConstructor
public class createUserRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email(message = "Invalid Email")
    @NotBlank
    private String email;
    @NotNull
    private long phone;
    @NotBlank
    private String password;
}
