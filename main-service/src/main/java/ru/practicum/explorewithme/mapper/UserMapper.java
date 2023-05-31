package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.User;

@UtilityClass
public class UserMapper {
    public User toModel(UserRequestDto requestDto) {
        return new User(requestDto.getName(), requestDto.getEmail());
    }

    public UserResponseDto toResponseDto(User model) {
        return new UserResponseDto(model.getId(), model.getName(), model.getEmail());
    }
}
