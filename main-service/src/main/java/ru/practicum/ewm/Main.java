package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
@Primary
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}