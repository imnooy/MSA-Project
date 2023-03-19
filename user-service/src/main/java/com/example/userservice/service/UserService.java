package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.jpa.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


//authentication의 인증하기 위한 자격으로 들어갈려면 UserDetailsService 상속필요!
public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();

    UserDto getUserDetailsByEmail(String email);
}
