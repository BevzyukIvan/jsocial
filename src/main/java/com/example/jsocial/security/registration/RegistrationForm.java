package com.example.jsocial.security.registration;

import com.example.jsocial.model.user.Role;
import com.example.jsocial.model.user.User;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class RegistrationForm {

    private String username;

    @Size(min = 6, message = "Пароль повинен містити мінімум 6 символів")
    private String password;

    private String confirmPassword;

    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    public User toUser(PasswordEncoder encoder) {
        return new User(username, encoder.encode(password), Role.ROLE_USER);
    }
}
