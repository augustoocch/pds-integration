package ar.com.uade.pds.final_project.users.service;

import ar.com.uade.pds.final_project.domain.dto.request.UpdateProfileRequest;
import ar.com.uade.pds.final_project.domain.dto.response.UserDTO;
import ar.com.uade.pds.final_project.users.entity.User;

public interface DataService {
    boolean checkIsAuthenticated();
    String currentSessionToken();
    void updateProfile(UpdateProfileRequest request);
    UserDTO findDTOUser(Long id);
    UserDTO findDTOUserWithToken();
    User findUser(Long id);
}
