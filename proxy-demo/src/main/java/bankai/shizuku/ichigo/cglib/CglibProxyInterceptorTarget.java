package bankai.shizuku.ichigo.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 加入target object
 */
public class CglibProxyInterceptorTarget implements MethodInterceptor {
    public final Object delegate;

    public CglibProxyInterceptorTarget(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
//        System.out.println("目标对象:" +o);
//        o 参数：它代表的是代理对象本身。也就是说，当目标对象被 CGLIB 增强后，在调用目标方法时，intercept 方法会被触发，此时 o 就是这个经过增强后的代理对象。
        System.out.println("目标对象方法:" + method.getName());
        System.out.println("参数:" + Arrays.toString(objects));
        System.out.println("代理方法:" + methodProxy);
        System.out.println("==================");
        Object result = methodProxy.invoke(delegate, objects);
        System.out.println("==================");
        return result;
    }
}
