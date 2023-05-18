package com.ranen.service;

import com.spring.Component;

@Component("orderService")
//@Scope("prototype")
public class OrderService {
    public String print(){
        System.out.println("我是order");
        return null;
    }
}
