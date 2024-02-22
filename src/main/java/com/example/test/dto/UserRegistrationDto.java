package com.example.test.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto
{
    private Long id;
    @NotEmpty(message = "Введите имя")
    private String firstName;
    @NotEmpty(message = "Введите фамилию")
    private String lastName;
    @NotEmpty(message = "Введите логин")
    private String username;
    @NotEmpty(message = "Введите пароль")
    private String password;
}