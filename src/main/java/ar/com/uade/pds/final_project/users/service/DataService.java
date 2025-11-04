package ar.com.uade.pds.final_project.users.service;

import ar.com.uade.pds.final_project.domain.dto.response.UserDTO;
import ar.com.uade.pds.final_project.users.entity.User;

public interface DataService {
    boolean checkIsAuthenticated();
    UserDTO findDTOUserWithToken();
    User findUserWithToken();
}
