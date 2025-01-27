package com.example.springboot.controller;

import com.example.springboot.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/exception/custom")
    public void customException() {
        throw new CustomException("FUCK!", HttpStatus.BAD_REQUEST);
    }
}
