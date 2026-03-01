package com.adrs.dto;

import com.adrs.model.District;
import com.adrs.model.Province;
import com.adrs.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user creation and updates.
 *
 * <p>Uses validation groups to differentiate between create and update rules:
 * password is required on creation but optional on updates.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    /**
     * Validation group for user creation operations.
     */
    public interface Create {}

    /**
     * Validation group for user update operations.
     */
    public interface Update {}

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required", groups = Create.class)
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    private Province province;

    private District district;

    @NotNull(message = "Role is required")
    private User.Role role;

    private Boolean active = true;
}
