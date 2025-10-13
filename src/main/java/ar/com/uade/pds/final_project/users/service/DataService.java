package ar.com.uade.pds.final_project.users.service;

import ar.com.uade.pds.final_project.domain.dto.request.UpdateProfileRequest;
import ar.com.uade.pds.final_project.domain.dto.response.UserDTO;
import ar.com.uade.pds.final_project.users.entity.User;

public interface DataService {
    void updateProfile(UpdateProfileRequest request);
    UserDTO findDTOUser(Long id);
    User findUser(Long id);
}
