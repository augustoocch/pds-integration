package ar.com.uade.pds.final_project.users.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.UpdateProfileRequest;
import ar.com.uade.pds.final_project.domain.dto.response.UserDTO;
import ar.com.uade.pds.final_project.security.ISecurityValidator;
import ar.com.uade.pds.final_project.users.Business.SessionContext;
import ar.com.uade.pds.final_project.users.constants.UsersErrorDetails;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.DataService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DataServiceImpl implements DataService {

    private IUserRepository userRepository;
    private final SessionContext sessionContext;
    private final ISecurityValidator securityValidator;


    public boolean checkIsAuthenticated() {
        return sessionContext.isAuthenticated();
    }

    @Override
    public String currentSessionToken() {
        return sessionContext.getToken();
    }

    @Override
    public void updateProfile(UpdateProfileRequest request) {
        String token = this.sessionContext.getToken();
        User user = this.securityValidator.getUserFromToken(token);

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRegion(request.getRegion());
        this.userRepository.save(user);
    }

    @Override
    public UserDTO findDTOUser(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UsersException(UsersErrorDetails.USER_NOT_FOUND.getMessage()));

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
    public UserDTO findDTOUserWithToken() {
        String token = this.sessionContext.getToken();
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
        String token = this.sessionContext.getToken();
        User userFromToken = this.securityValidator.getUserFromToken(token);
        return this.userRepository.findByEmail(userFromToken.getEmail())
                .orElseThrow(() -> new UsersException(UsersErrorDetails.USER_NOT_FOUND.getMessage()));
    }
}
