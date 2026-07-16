package com.finance.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String notificationSettings;
}
