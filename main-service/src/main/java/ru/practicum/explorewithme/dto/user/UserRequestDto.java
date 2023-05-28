package ru.practicum.explorewithme.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDto {
    @NotBlank @Size(min = 2, max = 250)
    private String name;
    @Email @NotBlank @Size(min = 6, max = 254)
    private String email;
}
