package com.lartimes;

import com.lartimes.service.PaymentService;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/28 1:16
 */
public class Application {

    public static void main(String[] args) throws Exception {
        URLClassLoader customLoader = new URLClassLoader(
                new URL[]{Thread.currentThread().getContextClassLoader().getResource("")},
                ClassLoader.getSystemClassLoader().getParent()); // 父级设为扩展类加载器
        Class<?> aClass = customLoader.loadClass("com.lartimes.service.impl.CardPaymentServiceImpl");
        System.out.println(aClass);
        // 3. 创建实例（假设有无参构造函数）
        Object instance = aClass.getDeclaredConstructor().newInstance();
//        for (Method method : aClass.getMethods()) {
//            System.out.println(method);
//            System.out.println(method.getName());
//        }
        Method method = aClass.getMethod("doPay");
        method.invoke(instance);
//
        customLoader.close();

        ServiceLoader<PaymentService> load = ServiceLoader.load(PaymentService.class);
        for (PaymentService payService : load) {
            System.out.println(payService);
            payService.doPay();
        }

    }
}
