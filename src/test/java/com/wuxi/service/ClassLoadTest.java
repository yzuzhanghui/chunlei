package com.wuxi.service;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.sql.Driver;
import java.util.Date;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.Test;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.util.ReflectionUtils;

import com.wuxi.bean.vo.User;

public class ClassLoadTest {


    @Test
    public void classLoad() {
        System.out.println(ClassLoadTest.class.getClassLoader());
        ClassLoader classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    //根据全限定名加载二进制数据
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    //将二进制数据转化为class对象
                    return defineClass(name, b, 0, b.length);
                } catch (Exception e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };

        Object obj = null;
        try {
            obj = classLoader.loadClass("com.wuxi.service.ClassLoadTest").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(obj.getClass());
        //不同类加载器加载的类是不同的
        System.out.println(obj instanceof com.wuxi.service.ClassLoadTest);
    }

    @Test
    public void showInterface() {
        for (Class<?> intf : AbstractRefreshableConfigApplicationContext.class.getInterfaces()) {
            System.out.println(intf.getName());
        }
    }


    @Test
    public void move() {
        int count = Integer.SIZE - 3;
        System.out.println(count);
        System.out.println((1 << count) - 1);
        System.out.println((1 >>> count));
    }


    @Test
    public void load() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        System.out.println("current loader: " + loader + "  " + (loader instanceof ClassLoader));
        System.out.println("parent loader: " + loader.getParent() + "  " + (loader.getParent() instanceof ClassLoader));
        System.out.println("grandparent loader: " + loader.getParent().getParent() + "  " + (loader.getParent().getParent() instanceof ClassLoader));
    }

    @Test
    public void bootload() {
        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        for (URL u : urls) {
            System.out.println(u.getPath());
        }
        System.out.println("=================================");
        for (String s : System.getProperty("java.ext.dirs").split(":")) {
            System.out.println(s);
        }
        System.out.println("=================================");
        for (String s : System.getProperty("java.class.path").split(":")) {
            System.out.println(s);
        }

    }

    @Test
    public void contextClassLoader() {
//		//获取extclassloader
//	    ClassLoader extClassloader = ClassLoadTest.class.getClassLoader().getParent();
//	    System.out.println("extloader:"  +extClassloader);
//	    //设置当前线程上下文加载器为ext,而mysql的jar包是放到了classpath下，这样就加载不到mysql驱动类
//	    Thread.currentThread().setContextClassLoader(extClassloader);

        ServiceLoader<Driver> loader = ServiceLoader.load(Driver.class);
        Iterator<Driver> iterator = loader.iterator();
        while (iterator.hasNext()) {
            Driver driver = (Driver) iterator.next();
            System.out.println("driver:" + driver.getClass() + ",loader:" + driver.getClass().getClassLoader());
        }
        //当父类加载器需要加载子类加载器中的资源时候可以通过设置和获取线程上下文类加载器来实现
        //默认是AppClassLoader
        System.out.println("current thread contextloader:" + Thread.currentThread().getContextClassLoader());
        System.out.println("current loader:" + ClassLoadTest.class.getClassLoader());
        System.out.println("ServiceLoader loader:" + ServiceLoader.class.getClassLoader());
    }

    @Test
    public void reflect() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> clazz = loader.loadClass("com.wuxi.bean.vo.User");
            User user = (User) clazz.newInstance();

            Method setName = clazz.getMethod("setName", String.class);
            setName.invoke(user, "zhangsan");

            Method setSchool = clazz.getMethod("setSchool", String.class);
            setSchool.invoke(user, "外国语");
            Method setWorkDay = clazz.getSuperclass().getDeclaredMethod("setWorkDay", Date.class);
            setWorkDay.setAccessible(true);
            setWorkDay.invoke(user, new Date());
            System.out.println(user);

            Field workDay = clazz.getSuperclass().getDeclaredField("workDay");
            System.out.println(workDay.getClass());
            System.out.println(workDay.getDeclaringClass());
            System.out.println(workDay.getModifiers());
            System.out.println(Modifier.isStatic(workDay.getModifiers()));
            workDay.setAccessible(true);
            workDay.set(user, new Date());

            /*
             * 1、getMethod是拿到本类所有public方法（包括继承）
             * 2、getDeclaredMethod是拿到所有private protect public方法（不包括继承）
             * 3、要想拿到父类的private、protected方法,必须用getSuperclass().getDeclaredMethod
             * 4、method的invoke方法只能调用public方法，private、protected方法会报IllegalAccessException,要setAccessible(true)
             */
            System.out.println(user);
            for (Field f : clazz.getDeclaredFields()) {
                f.setAccessible(true);
                Object object = f.get(user);
                System.out.println(f.getName() + " : " + object);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getMethod() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> clazz = loader.loadClass("com.wuxi.bean.vo.User");
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
            for (Method method : methods) {
                System.out.println(method.getName());
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

