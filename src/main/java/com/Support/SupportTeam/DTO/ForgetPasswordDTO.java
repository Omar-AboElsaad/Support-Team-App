package com.Support.SupportTeam.DTO;

import lombok.Data;

@Data
public class ForgetPasswordDTO {
    private String email;
    private String newPassword;
    private String otp;
}
