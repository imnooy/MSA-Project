package com.example.userservice.security;

import com.example.userservice.filter.AuthenticationFilter;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity //Web Security 용도로 사용
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**").permitAll();
//        http.authorizeRequests().antMatchers("/**")
//                        .hasIpAddress("이 아이피로 오는 것만 허용")
//                                .and()
//                                        .addFilter(getAuthenticationFilter());
        http.addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable(); //프레임 에러 방지
    }

    //필터 작업
    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        //AuthenticationFilter에서 userService, env를 생성자로 주입받기 때문에
        //넣어줘야한다!!
        AuthenticationFilter authenticationFilter=new AuthenticationFilter(userService, env);
        authenticationFilter.setAuthenticationManager(authenticationManager()); //spring security에서 가져온 매니저 지정해주기

        return authenticationFilter;
    }

    //인증
    //select pwd from users where email=? 로 검색해옴
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //db에는 인코딩한 비밀번호가 저장되어있음
        //현재 사용자가 가지고 있는 비밀번호와 db 비밀번호 비교
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
