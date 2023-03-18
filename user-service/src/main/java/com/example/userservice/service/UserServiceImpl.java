package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.jpa.UserEntity;
import com.example.userservice.service.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity=userRepository.findByUserId(userId);

        if(userEntity==null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<ResponseOrder> orders=new ArrayList<>();

        UserDto userDto=new ModelMapper().map(userEntity, UserDto.class);
        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }
}
