package com.protec.recervhotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RecervHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecervHotelApplication.class, args);
    }

}
