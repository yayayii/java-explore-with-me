package ru.practicum.explorewithme.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.model.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class AdminUserService {
    private final UserDao userDao;


    //users
    @Transactional
    public UserResponseDto addUser(UserRequestDto requestDto) {
        log.info("main-service - AdminService - addUser - requestDto: {}", requestDto);
        User user = UserMapper.toModel(requestDto);
        return UserMapper.toResponseDto(userDao.save(user));
    }

    public List<UserResponseDto> getUsers(List<Long> ids, int from, int size) {
        log.info("main-service - AdminService - getUsers - uris: {} / from: {} / size: {}", ids, from, size);

        if (ids == null) {
            return userDao.findAll(PageRequest.of(from, size))
                    .stream().map(UserMapper::toResponseDto).collect(Collectors.toList());
        } else {
            return userDao.findAllByIdIn(ids, PageRequest.of(from, size))
                    .stream().map(UserMapper::toResponseDto).collect(Collectors.toList());
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("main-service - AdminService - deleteUser - userId: {}", userId);
        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        userDao.deleteById(userId);
    }
}
