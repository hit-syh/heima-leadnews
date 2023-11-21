package com.heima.app.gateway.filter;

import com.heima.utils.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
@Order
public class AuthorizeFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.判断是否是登录
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if(request.getURI().getPath().contains("/login"))
        {
            return chain.filter(exchange);
        }
        //2.判断token是否有效
        String token = request.getHeaders().getFirst("token");
        if(StringUtils.isBlank(token))
        {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);
            if(result==1 || result==2)
            {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();

            }
            //将用户id放入到header中
            Object userId = claimsBody.get("id");
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", userId + "");
            }).build();
//            log.info("用户{}发来请求",userId);
            exchange.mutate().request(serverHttpRequest);
            }catch (Exception e)
            {
                e.printStackTrace();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }



        return chain.filter(exchange);
    }
}
