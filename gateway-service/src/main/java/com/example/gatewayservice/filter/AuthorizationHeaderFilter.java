package com.example.gatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component //필터 등록
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    private Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env=env;
    }

    //필터 만들려면 apply 함수 필요!
    //요청되는 모든 정보에 대해 이 필터 실행하게 해주삼
    //login -> token -> users (with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            //WebFlux에서는 Servlet이라는 개념 사용X
            ServerHttpRequest request=exchange.getRequest(); //사용자가 헤더에 로그인했을 때 받은 토큰 전달함
            //이 토큰 맞는지


            //헤더에 토큰이 없을 때
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            //있는 인증 정보 가져오기
            String authorizationHeader=request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            //String 값으로 들어옴 -> Bearer 토큰이 있기 때문에 "Bearer" 말고 나머지 부분이 토큰
            String jwt=authorizationHeader.replace("Bearer", "");

            if(!isJwtValid(jwt)) { //토큰이 유효하지않을 때
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    //webflux에서 비동기 처리
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response=exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete(); //해당 응답 종료
    }

    //맞다 틀리다
    //
    private boolean isJwtValid(String jwt) {
        boolean returnValue=true;

        String subject=null;

        try {
            subject= Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject(); //디코딩해서 나오는 subject 구하기
            //이게 userId와 같으면 됨
        }catch(Exception ex) {
            returnValue=false;
        }

        if(subject==null || subject.isEmpty()) {
            returnValue=false;
        }

        return returnValue;
    }

    public static class Config {
        // Put the configuration properties
    }
}
