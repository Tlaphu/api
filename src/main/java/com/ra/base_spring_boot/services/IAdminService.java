package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormUpdateCompany;
import com.ra.base_spring_boot.dto.req.FormUpdateProfile;
import com.ra.base_spring_boot.dto.resp.*;

import java.util.List;

public interface IAdminService {
    JwtResponse login(FormLogin formLogin);

    List<CompanyResponse> findAll();

    void deleteCompany(Long id);

    CompanyResponse updateCompany(Long id, FormUpdateCompany form);

    List<CandidateResponse> findAllCandidates();

    CandidateResponse updateCandidate(Long id, FormUpdateProfile form);

    void deleteCandidate(Long id);

    List<AccountCompanyResponse> findAllAccountsCompany();

    void deleteAccountCompany(Long id);

    List<AddressCompanyResponse> getAllByCompanyId(Long companyId);
    AddressCompanyResponse create(Long companyId, FormAddressCompany form);
    AddressCompanyResponse update(Long id, FormAddressCompany form);
    void delete(Long id);
    boolean activateCompanyAccount(Long id);
}
