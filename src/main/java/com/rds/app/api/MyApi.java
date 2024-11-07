package com.rds.app.api;

import com.rds.app.dto.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class MyApi {

    @GetMapping
    public Message hello() {
        return new Message("Hello Sir/Madam, welcome to UST!");
    }
}
