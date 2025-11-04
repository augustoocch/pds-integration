package ar.com.uade.pds.final_project.users.service.impl;

import ar.com.uade.pds.final_project.domain.dto.response.UserDTO;
import ar.com.uade.pds.final_project.security.ISecurityValidator;
import ar.com.uade.pds.final_project.users.business.SessionContext;
import ar.com.uade.pds.final_project.users.constants.UsersErrorDetails;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.DataService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DataServiceImpl implements DataService {

    private IUserRepository userRepository;
    private final ISecurityValidator securityValidator;


    public boolean checkIsAuthenticated() {
        return SessionContext.getInstance().isAuthenticated();
    }

    @Override
    public UserDTO findDTOUserWithToken() {
        String token = SessionContext.getInstance().getToken();
        User user = this.securityValidator.getUserFromToken(token);

        return new UserDTO.Builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .preferredRoles(user.getPreferredRoles())
                .region(user.getRegion())
                .preference(user.getPreference())
                .build();
    }

    @Override
    public User findUserWithToken() {
        String token = SessionContext.getInstance().getToken();
        User userFromToken = this.securityValidator.getUserFromToken(token);
        return this.userRepository.findByEmail(userFromToken.getEmail())
                .orElseThrow(() -> new UsersException(UsersErrorDetails.USER_NOT_FOUND.getMessage()));
    }
}
