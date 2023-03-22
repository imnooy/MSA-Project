package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.service.jpa.UserEntity;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestLogin;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {
    private Environment env;
    private UserService userService;
    @Autowired //Autowired로 주입 - 권장 x
    private Greeting greeting;

    @Autowired //생성자로 주입
    public UserController(Environment env, UserService userService) {
        this.env=env;
        this.userService=userService;
    }
    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service"       + ", port(local.server.port="+ env.getProperty("local.server.port")
                + ", port(server.port="+ env.getProperty("server.port")
                + ", token secret="+ env.getProperty("token.secret")
                + ", token expiration time"+ env.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    public String welcome() {
//        return env.getProperty("greeting.message");

        //Greeting 객체 쓰기
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto=mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser=mapper.map(userDto, ResponseUser.class);

        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        Iterable<UserEntity> userList=userService.getUserByAll();
        List<ResponseUser> result=new ArrayList<>();

        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable("userId") String userId) {
        UserDto userDto=userService.getUserByUserId(userId);
        ResponseUser returnUser=new ModelMapper().map(userDto, ResponseUser.class);

        return new ResponseEntity<>(returnUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestLogin requestLogin) {
        return null;
    }
}
