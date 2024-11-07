package com.rds.app.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record Message(String message, String date, String time) {
    public Message(String message) {
        this(message, LocalDate.now().toString(), LocalTime.now().toString());
    }
}
