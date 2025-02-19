package com.Support.SupportTeam.Request;

import lombok.Data;

@Data
public class ChangePasswordDTO {

        private String email;
        private String currentPassword;
        private String newPassword;
    }

