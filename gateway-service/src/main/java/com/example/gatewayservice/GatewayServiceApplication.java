package com.example.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        //사용자가 요청한 trace 정보가 메모리에 담겨서
        //우리가 필요할 때 엔드포인트로 확인 가능
        return new InMemoryHttpTraceRepository();
    }

}
