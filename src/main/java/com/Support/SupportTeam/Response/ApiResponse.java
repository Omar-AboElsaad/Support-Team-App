package com.Support.SupportTeam.Response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse {
    private String Message;
    private Object data;

    public ApiResponse(String message,Object data){
        this.Message=message;
        this.data=data;
    }
}