package ru.practicum.ewm.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;

    @NotBlank
    @Size(min = 6, max = 254)
    @Email
    private String email;

}