package com.finance.app.service;

import com.finance.app.dto.ChangePasswordRequest;
import com.finance.app.dto.ProfileDto;
import com.finance.app.entity.User;
import com.finance.app.exception.BadRequestException;
import com.finance.app.exception.ResourceNotFoundException;
import com.finance.app.repository.UserRepository;
import com.finance.app.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            String username = ((UserDetailsImpl) principal).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        }
        throw new RuntimeException("Invalid authentication principal");
    }

    public ProfileDto getProfile() {
        User user = getAuthenticatedUser();
        ProfileDto dto = new ProfileDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setNotificationSettings(user.getNotificationSettings());
        return dto;
    }

    public ProfileDto updateProfile(ProfileDto dto) {
        User user = getAuthenticatedUser();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(dto.getProfilePicture());
        }
        if (dto.getNotificationSettings() != null) {
            user.setNotificationSettings(dto.getNotificationSettings());
        }
        userRepository.save(user);
        return getProfile();
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
