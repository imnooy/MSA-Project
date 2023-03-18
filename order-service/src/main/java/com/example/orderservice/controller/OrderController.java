package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.jpa.OrderEntity;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final Environment env;

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return String.format("It's Working in Order Service on Port %s", request.getServerPort());
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<?> createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto=mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createOrder=orderService.createOrder(orderDto);

        ResponseOrder responseOrder=mapper.map(createOrder, ResponseOrder.class);

        return new ResponseEntity<>(responseOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<?> getOrder(@PathVariable("userId") String userId) {
        Iterable<OrderEntity> orderList=orderService.getOrdersByUserId(userId);
        List<ResponseOrder> result=new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
