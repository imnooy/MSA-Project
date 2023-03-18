package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.jpa.UserEntity;
import com.example.userservice.service.jpa.UserRepository;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository=userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }
    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString()); //랜덤 아이디 부여
        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity user= mapper.map(userDto, UserEntity.class); //매핑!
        String encryptedPwd=bCryptPasswordEncoder.encode(userDto.getPwd());
        user.setEncryptedPwd(encryptedPwd);
        userDto.setEncryptedPwd(encryptedPwd);

        userRepository.save(user);

        return userDto;
    }
}
