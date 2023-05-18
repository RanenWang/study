package com.ranen;

import com.mybatis.Select;

import java.util.List;

public interface UserMapper {
    @Select("select * from users where name = #{name}")
    public List<User> getUser(String name, Integer age);
}
