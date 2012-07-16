package play.remotemock.processor;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import play.remotemock.Remotable;
import play.remotemock.util.RmiRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans application context for <code>Remotable</code> beans by checking
 * presence of Remotable annotation and implementation of Remotable interface.
 * Exports the beans using <code>Remotable</code> interface
 * <p/>
 * Remotable interface implementations are returned as they are, since they contain their own proxying logic.
 * Remotable annotated classes are automatically proxied using a default implementation.
 */
public class RmiRemotableBeanProcessor implements BeanPostProcessor, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RmiRemotableBeanProcessor.class);

    private final RmiRegistry rmiRegistry;

    private int order = 20;

    private RmiExportableBeanProcessor.RmiServiceNamingStrategy namingStrategy =
            new DefaultRemotableServiceNamingStrategy();

    public RmiRemotableBeanProcessor(RmiRegistry rmiRegistry) {
        this.rmiRegistry = rmiRegistry;
    }

    public void init() {
        logger.info("Activated. Will export remotable objects");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        /* let local object be initialised properly */
        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object result = bean;
        Class<play.remotemock.annotation.Remotable> remotableAnnotation = play.remotemock.annotation.Remotable.class;
        try {
            if (bean instanceof Remotable && !Proxy.isProxyClass(bean.getClass())) {
                rmiRegistry.exportService((Remotable) bean,
                        namingStrategy.nameService(bean, beanName),
                        Remotable.class);
                result = bean;
            } else if (bean.getClass().isAnnotationPresent(remotableAnnotation)) {
                Class<?> serviceInterface = bean.getClass().getAnnotation(remotableAnnotation).value();
                if (!(serviceInterface.isInterface() && serviceInterface.isInstance(bean))) {
                    throw new IllegalArgumentException("Bean does not implement interface used in Remotable annotation." +
                            " Bean: " + beanName + " interface: " + serviceInterface);
                }

                List<Class<?>> interfaces = new ArrayList<Class<?>>();
                interfaces.add(Remotable.class);
                interfaces.addAll(ClassUtils.getAllInterfaces(bean.getClass()));

                InvocationHandler handler = new RemotableInvocationHandler<Object>(bean,
                        (Class<Object>) serviceInterface);

                result = Proxy.newProxyInstance(getClass().getClassLoader(),
                        interfaces.toArray(new Class<?>[interfaces.size()]),
                        new RmiArgumentEnhancer(handler, rmiRegistry));

                rmiRegistry.exportService((Remotable) result,
                        namingStrategy.nameService(bean, beanName),
                        Remotable.class);
            }
        } catch (RemoteException e) {
            logger.warn("Unable to proxy remotable bean: " + beanName, e);
        }
        return result;
    }

    public void setNamingStrategy(RmiExportableBeanProcessor.RmiServiceNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    static class DefaultRemotableServiceNamingStrategy extends
            RmiExportableBeanProcessor.DefaultRmiServiceNamingStrategy {

        @Override
        public String nameService(Object bean, String beanName) {
            return super.nameService(bean, beanName) + "Remote";
        }

    }

}
