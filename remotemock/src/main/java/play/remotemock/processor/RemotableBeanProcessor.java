package play.remotemock.processor;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import play.remotemock.annotation.Remotable;
import play.remotemock.util.RmiRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;


public class RemotableBeanProcessor implements BeanPostProcessor, Ordered {

    private RmiRegistry rmiRegistry;

    private ServiceNamingStrategy namingStrategy = new DefaultRemotableServiceNamingStrategy();

    private int order = 10;

    public RemotableBeanProcessor(RmiRegistry rmiRegistry) {
        this.rmiRegistry = rmiRegistry;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Remotable remotable = bean.getClass().getAnnotation(Remotable.class);
        try {
            if (remotable != null) {
                Class<?>[] allInterfaces = ArrayUtils.addAll(ClassUtils.getAllInterfaces(bean),
                        play.remotemock.Remotable.class);
                InvocationHandler invocationHandler = new RemotableInvocationHandler<>(bean,
                        (Class<Object>) remotable.value());

                Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), allInterfaces, invocationHandler);

                rmiRegistry.exportService((play.remotemock.Remotable) proxy,
                        namingStrategy.getServiceName(bean, beanName, remotable.value()),
                        play.remotemock.Remotable.class);
                return proxy;
            } else if (bean instanceof play.remotemock.Remotable) {
                rmiRegistry.exportService((play.remotemock.Remotable) bean,
                        namingStrategy.getServiceName(bean, beanName, bean.getClass()),
                        play.remotemock.Remotable.class);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setNamingStrategy(ServiceNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    private class DefaultRemotableServiceNamingStrategy extends ExportableBeanProcessor.DefaultServiceNamingStrategy {
        @Override
        public String getServiceName(Object bean, String beanName, Class<?> serviceInterface) {
            return super.getServiceName(bean, beanName, serviceInterface) + "Remote";
        }
    }
}
