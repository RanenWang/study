package com.ranen.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.Proxy;

@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        if (beanName.equals("userService")) {
            UserServiceImpl userService = (UserServiceImpl) bean;
            userService.setAge(12);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        // return bean;
        if (beanName.equals("userService")) {
            Object proxyInstance = Proxy.newProxyInstance(CustomBeanPostProcessor.class.getClassLoader(),
                    bean.getClass().getInterfaces(),
                    (proxy, method, args) -> {
                        System.out.println("代理逻辑");
                        return method.invoke(bean, args);
                    });
            return proxyInstance;
        }
        return bean;
    }
}
