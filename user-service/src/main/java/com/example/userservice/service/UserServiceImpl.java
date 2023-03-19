package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.jpa.UserEntity;
import com.example.userservice.service.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

    //이메일 가지고 사용자 찾아오는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity=userRepository.findByEmail(username);

        if(userEntity==null) throw new UsernameNotFoundException(username);

        //new ArrayList<>(): 로그인 되었을 때 할 수 있는 작업 중에서 권한을 추가하는 작업 넣으면 됨
        //지금은 권한 없어서 걍 빈 리스트
        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), true, true, true, true, new ArrayList<>());
    }
}
