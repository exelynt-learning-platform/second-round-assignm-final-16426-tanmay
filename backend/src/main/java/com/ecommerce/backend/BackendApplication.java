package com.ecommerce.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// FIX: @SpringBootApplication is placed on the class in the ROOT package
// (com.ecommerce.backend).  Component scanning therefore covers every sub-package:
//   com.ecommerce.backend.entity
//   com.ecommerce.backend.repository
//   com.ecommerce.backend.config
//   com.ecommerce.backend.controller
//   com.ecommerce.backend.service
// Previously, files in bare packages ("entity", "config", "controller", etc.)
// were OUTSIDE this scan path and were never picked up by Spring.
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
