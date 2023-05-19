package ru.practicum.explorewithme.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDto {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String name;
}