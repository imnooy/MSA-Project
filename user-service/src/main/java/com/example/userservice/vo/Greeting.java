package com.example.userservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
//@AllArgsConstructor

//yml에 사용한 환경 변수 불러오는 클래스
public class Greeting {
    @Value("${greeting.message}")
    private String message;
}
