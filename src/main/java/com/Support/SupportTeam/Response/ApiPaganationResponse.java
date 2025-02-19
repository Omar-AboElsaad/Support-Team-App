package com.Support.SupportTeam.Response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiPaganationResponse {
    private String Message;
    private Object data;
    private long totalProjects;
    private int totalPages;

    public ApiPaganationResponse(String message, Object data, long totalProjects, int totalPages){
        this.Message=message;
        this.data=data;
        this.totalProjects = totalProjects;
        this.totalPages = totalPages;
    }
}