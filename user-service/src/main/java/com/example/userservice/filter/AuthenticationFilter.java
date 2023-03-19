package com.example.userservice.filter;

import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //post로 전달되는 dto는 Request parameter로 받을 수 없기 때문에
            //inputstream으로 처리하면 어떤 값이 들어왔는지 알 수 있다!
            RequestLogin cred=new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
            return getAuthenticationManager().authenticate( //manager한테 인증 작업 요청
                    new UsernamePasswordAuthenticationToken(
                            cred.getEmail(),
                            cred.getPassword(), //토큰에 저장하고 인증
                            new ArrayList<>() //이 리스트는 권한 - 어떤 권한을 가질 것인지
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //인증되었을 때 뭐할건지
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //인증 결과에서 username 로그 찍어보자
        log.debug(((User)authResult.getPrincipal()).getUsername());
    }
}
