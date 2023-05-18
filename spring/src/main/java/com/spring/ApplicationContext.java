package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;
        // 扫描，创建beanDefinitionMap
        scan(configClass);
        for (Map.Entry<String, BeanDefinition> stringBeanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = stringBeanDefinitionEntry.getKey();
            BeanDefinition beanDefinition = stringBeanDefinitionEntry.getValue();
            if (beanDefinition.getScope().equals("singleton")) {
                // 单例需创建
                Object o = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, o);
            }
        }


    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        Object instance = null;
        try {
            // BeanPostProcessor
            // 实例化
            instance = clazz.getDeclaredConstructor().newInstance();
            // 依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(field.getName());
                    field.setAccessible(true);
                    field.set(instance, bean);
                }
            }
            // aware
            if (instance instanceof BeanNameAware) {
                BeanNameAware beanNameAware = (BeanNameAware) instance;
                beanNameAware.setBeanName(beanName);
            }
            // 初始化前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }
            // 初始化
            if (instance instanceof InitializingBean) {
                InitializingBean initializingBean = (InitializingBean) instance;
                initializingBean.afterPropertiesSet();
            }
            // 初始化后
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }
            // AOP
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    private void scan(Class configClass) {
        ComponentScan componentScan = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScan.value();
        path = path.replace(".", "/");
        System.out.println("扫描路径：" + path);
        /*
        类加载器
        bootstrap jre/lib
        ext jre/ext/lib
        app classpath
         */
        // appClassLoader
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(path);
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");
                    System.out.println(className);
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessors.add(beanPostProcessor);
                            }
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();
                            System.out.println("扫描到了一个bean：name is " + className);
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            } else {
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }

                }


            }
        }
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object o = singletonObjects.get(beanName);
                return o;
            } else {
                Object o = createBean(beanName, beanDefinition);
                return o;
            }
        } else {
            throw new RuntimeException("不存在该定义");
        }
    }
}
