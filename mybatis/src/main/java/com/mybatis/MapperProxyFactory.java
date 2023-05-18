package com.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理工厂
 * @author ranen
 */
public class MapperProxyFactory {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T getMapper(Class mapper){
        Object proxyInstance =
        Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{mapper}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // step 1 解析
                // step 2 执行
                // step 3 结果返回
                return method.invoke(proxy,args);
            }
        });
        return (T)proxyInstance;
    }
}
