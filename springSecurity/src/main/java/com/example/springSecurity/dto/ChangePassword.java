package com.example.springSecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePassword {
	
	    @NotBlank(message = "Current password is required")
	    private String currentPassword;

	    @NotBlank(message = "New password is required")
	    private String newPassword;

	    @NotBlank(message = "Confirm password is required")
	    private String confirmPassword;

}
