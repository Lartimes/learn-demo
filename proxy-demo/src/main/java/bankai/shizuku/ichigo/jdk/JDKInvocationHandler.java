package bankai.shizuku.ichigo.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JDKInvocationHandler implements InvocationHandler {
    private final Object target;

    public JDKInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("==================");
        System.out.println("执行invoke");
        Object result = method.invoke(target, args);
        System.out.println("==================");
        return result;
    }
}
