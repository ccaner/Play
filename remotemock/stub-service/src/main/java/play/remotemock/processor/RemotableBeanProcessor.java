package play.remotemock.processor;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import play.remotemock.annotation.Remotable;
import play.remotemock.util.RmiRegistry;

import java.rmi.RemoteException;

public class RemotableBeanProcessor implements BeanPostProcessor, ApplicationContextAware {

    ApplicationContext context;

    RmiRegistry rmiRegistry;

    ServiceNamingStrategy namingStrategy = new DefaultRemotableServiceNamingStrategy();

    public RemotableBeanProcessor(RmiRegistry rmiRegistry) {
        this.rmiRegistry = rmiRegistry;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Remotable remotable = bean.getClass().getAnnotation(Remotable.class);
        try {
            if (remotable != null) {
                Class<?>[] interfaces = new Class<?>[]{play.remotemock.Remotable.class};
                Class<?> superClass = bean.getClass();
                MethodInterceptor invocationHandler = new RemotableInvocationHandler<Object>(bean,
                        (Class<Object>) remotable.value());

                Object proxy = proxyObject(interfaces, superClass, invocationHandler);
                rmiRegistry.exportService((play.remotemock.Remotable) proxy, namingStrategy.getServiceName(bean, beanName, remotable.value()),
                        play.remotemock.Remotable.class);
                return proxy;
            } else if (bean instanceof play.remotemock.Remotable) {
                rmiRegistry.exportService((play.remotemock.Remotable) bean, namingStrategy.getServiceName(bean, beanName, bean.getClass()),
                        play.remotemock.Remotable.class);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bean;
    }

    private Object proxyObject(Class<?>[] interfaces, Class<?> superClass, MethodInterceptor invocationHandler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(superClass);
        enhancer.setInterfaces(interfaces);
        enhancer.setCallback(invocationHandler);
        return enhancer.create();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public void setNamingStrategy(ServiceNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    private class DefaultRemotableServiceNamingStrategy extends ExportableBeanProcessor.DefaultServiceNamingStrategy {
        @Override
        public String getServiceName(Object bean, String beanName, Class<?> serviceInterface) {
            return super.getServiceName(bean, beanName, serviceInterface) + "Remote";
        }
    }
}
