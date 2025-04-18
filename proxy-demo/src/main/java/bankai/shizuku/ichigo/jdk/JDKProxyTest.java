package bankai.shizuku.ichigo.jdk;

import bankai.shizuku.ichigo.common.MyInterface;
import bankai.shizuku.ichigo.common.MyInterfaceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JDKProxyTest {
    public static void main(String[] args) {
        MyInterface myInterface = new MyInterfaceImpl();
        JDKInvocationHandler jdkInvocationHandler = new JDKInvocationHandler(myInterface);
        MyInterface proxyInstance = (MyInterface) Proxy.newProxyInstance(myInterface.getClass().getClassLoader(),  //目标类加载器
                myInterface.getClass().getInterfaces(), //目标类接口
                jdkInvocationHandler);
        boolean proxyClass = Proxy.isProxyClass(proxyInstance.getClass());
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxyInstance);
        System.out.println("invocationHandler: " +invocationHandler);

        System.out.println("是否是代理类: " + proxyClass);
        proxyInstance.doSomething();
    }


}
