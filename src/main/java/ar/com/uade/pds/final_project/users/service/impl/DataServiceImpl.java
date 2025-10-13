package ar.com.uade.pds.final_project.users.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.UpdateProfileRequest;
import ar.com.uade.pds.final_project.domain.dto.response.UserDTO;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.DataService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DataServiceImpl implements DataService {

    private IUserRepository userRepository;


    @Override
    public void updateProfile(UpdateProfileRequest request) {

    }

    @Override
    public UserDTO findDTOUser(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UsersException("User not found"));

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .preferredRoles(user.getPreferredRoles())
                .region(user.getRegion())
                .preference(user.getPreference())
                .build();
    }

    @Override
    public User findUser(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UsersException("User not found"));
    }
}
