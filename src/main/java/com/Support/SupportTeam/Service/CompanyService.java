package com.Support.SupportTeam.Service;

import com.Support.SupportTeam.CustomExceptions.ResourceAlreadyExistException;
import com.Support.SupportTeam.CustomExceptions.ResourceNotFoundException;
import com.Support.SupportTeam.DTO.CompanyDTO;
import com.Support.SupportTeam.DTO.ProjectDTO;
import com.Support.SupportTeam.Entity.Company;
import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Repository.CompanyRepo;
import com.Support.SupportTeam.Repository.ProjectRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CompanyService {
    private final CompanyRepo companyRepo;
    private final ModelMapper modelMapper;
    private final ProjectRepo projectRepo;




    public Company findByName(String companyName){
       return companyRepo.findByName(companyName)
                .orElseThrow(() -> new ResourceNotFoundException("There is no Company with name "+companyName));
    }
//----------------------------------------------------------------------------------------------------------------------

    public Company findById(Long companyId){
        return companyRepo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no Company with name "+companyId));
    }

//----------------------------------------------------------------------------------------------------------------------

    public Company createCompany(String companyName){
        if (companyRepo.existsByName(companyName)) {
            throw new ResourceAlreadyExistException("This company already added");
        }

        Company company=Company.builder()
                .name(companyName)
                .build();

        return companyRepo.save(company);
    }

//----------------------------------------------------------------------------------------------------------------------

    public Company updateCompany(String companyName,String newCompanyName){
        if (companyRepo.existsByName(newCompanyName)) {
            throw new ResourceAlreadyExistException("There is a company with name "+newCompanyName + " you can use it");
        }

        Company company=findByName(companyName);

        company.setName(newCompanyName);

        return companyRepo.save(company);
    }


//----------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void deleteCompany(String companyName){
        Company company=findByName(companyName);

        for (Project project : company.getProjects()) {
            project.setCompany(null);
            projectRepo.save(project); // Update project
        }

        companyRepo.delete(company);
    }

//----------------------------------------------------------------------------------------------------------------------

    public CompanyDTO ConvertCompanyToCompanyDto(Company company) {
        return modelMapper.map(company, CompanyDTO.class);
    }

}
