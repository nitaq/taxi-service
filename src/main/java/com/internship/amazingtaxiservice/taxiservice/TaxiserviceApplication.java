package com.internship.amazingtaxiservice.taxiservice;

import com.internship.amazingtaxiservice.taxiservice.model.Role;
import com.internship.amazingtaxiservice.taxiservice.model.Status;
import com.internship.amazingtaxiservice.taxiservice.service.RoleService;
import com.internship.amazingtaxiservice.taxiservice.service.StatusService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaxiserviceApplication {

    @Autowired
    private RoleService roleService;

    @Autowired
    private StatusService statusService;

    public static void main(String[] args) {
        SpringApplication.run(TaxiserviceApplication.class, args);
    }

//    @Bean
//    InitializingBean sendDatabase() {
//        return () -> {
//            roleService.saveInitialData(new Role(1,"ADMIN"));
//            roleService.saveInitialData(new Role(2,"USER"));
//            statusService.saveInitialData(new Status(1,"FREE"));
//            statusService.saveInitialData(new Status(2,"BUSY"));
//            statusService.saveInitialData(new Status(3,"OFF"));
//        };
//    }



}




