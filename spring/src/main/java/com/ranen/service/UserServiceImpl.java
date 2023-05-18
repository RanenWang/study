package com.ranen.service;

import com.spring.*;

@Component("userService")
//@Scope("prototype")
public class UserServiceImpl implements BeanNameAware, InitializingBean, UserService {
    @Autowired
    OrderService orderService;
    private String beanName;

    private Integer age;

    public void print() {
        System.out.println(this.toString());
        orderService.print();
        System.out.println(beanName);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    // 初始化
    @Override
    public void afterPropertiesSet() {
        System.out.println("user 初始化");
    }

    public void setAge(int i) {
        age = i;
    }

    @Override
    public String toString() {
        return "UserService{" +
                "orderService=" + orderService +
                ", beanName='" + beanName + '\'' +
                ", age=" + age +
                '}';
    }
}
