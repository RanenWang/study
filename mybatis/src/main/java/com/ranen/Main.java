package com.ranen;

import com.mybatis.MapperProxyFactory;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        UserMapper userMapper = MapperProxyFactory.getMapper(UserMapper.class);
        List<User> userList = userMapper.getUser("ranen",1);
        System.out.println(userList);
    }
}