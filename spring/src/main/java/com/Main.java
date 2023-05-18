package com;

import com.ranen.service.UserService;
import com.ranen.service.UserServiceImpl;
import com.spring.AppConfig;
import com.spring.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        //userService.print();

    }
}