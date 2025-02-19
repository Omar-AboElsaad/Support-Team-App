package com.Support.SupportTeam.Controller;

import com.Support.SupportTeam.DTO.CompanyDTO;
import com.Support.SupportTeam.Entity.Company;
import com.Support.SupportTeam.Response.ApiResponse;
import com.Support.SupportTeam.Service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.OK;

@AllArgsConstructor
@RestController
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/get-by-name")
    public ResponseEntity<ApiResponse> findByName(@RequestParam String companyName){
        Company company= companyService.findByName(companyName);
        CompanyDTO companyDTO=companyService.ConvertCompanyToCompanyDto(company);
        return ResponseEntity.status(OK).body(new ApiResponse("Founded",companyDTO));
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("/get-by-id")
    public ResponseEntity<ApiResponse> findById(@RequestParam Long companyId){
        Company company= companyService.findById(companyId);
        CompanyDTO companyDTO=companyService.ConvertCompanyToCompanyDto(company);
        return ResponseEntity.status(OK).body(new ApiResponse("Founded",companyDTO));
    }

//----------------------------------------------------------------------------------------------------------------------

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createCompany(@RequestParam String companyName) {
        Company company = companyService.createCompany(companyName);
        CompanyDTO companyDTO=companyService.ConvertCompanyToCompanyDto(company);
        return ResponseEntity.status(OK).body(new ApiResponse("Created",companyDTO));
    }


//----------------------------------------------------------------------------------------------------------------------

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateCompany(@RequestParam String companyName,@RequestParam String newName) {
        Company company = companyService.updateCompany(companyName,newName);
        CompanyDTO companyDTO=companyService.ConvertCompanyToCompanyDto(company);
        return ResponseEntity.status(OK).body(new ApiResponse("Updated",companyDTO));
    }


//----------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/delete-by-name")
    public ResponseEntity<ApiResponse> deleteCompany(@RequestParam String companyName){
        companyService.deleteCompany(companyName);
        return ResponseEntity.status(OK).body(new ApiResponse("Deleted successfully",null));

    }



}
