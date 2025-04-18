package bankai.shizuku.ichigo.cglib;

import bankai.shizuku.ichigo.common.MyInterface;
import bankai.shizuku.ichigo.common.MyInterfaceImpl;
import bankai.shizuku.ichigo.common.MyInterfaceImpl2;
import net.sf.cglib.proxy.Enhancer;

public class CglibEnhancerTest {
    public static void main(String[] args) throws Exception {
//        只要设定superclass 即可 ，直接生成代理类
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MyInterfaceImpl.class); //superclass
        MyInterfaceImpl target = new MyInterfaceImpl();
        enhancer.setCallback(new CglibProxyInterceptorTarget(target)); //
        MyInterface myInterface = (MyInterface) enhancer.create();
        myInterface.doSomething();
//        ====================================================
        Enhancer enhancer2 = new Enhancer();
        enhancer2.setSuperclass(MyInterfaceImpl2.class);
        enhancer2.setCallback(new CglibProxyInterceptorNoTarget());
        MyInterface anInterface2 = (MyInterface) enhancer2.create();
        anInterface2.doSomething();
    }
}
