package play.remotemock.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import play.remotemock.annotation.Exportable;
import play.remotemock.util.RmiRegistry;

import java.rmi.RemoteException;

/**
 * Scans application context for <code>Exportable</code> beans.
 * And exports them over rmi using their bean names as service name.
 */
public class RmiExportableBeanProcessor implements BeanPostProcessor, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RmiExportableBeanProcessor.class);

    private final RmiRegistry rmiRegistry;

    private RmiServiceNamingStrategy namingStrategy = new DefaultRmiServiceNamingStrategy();

    private int order = 10;

    public RmiExportableBeanProcessor(RmiRegistry rmiRegistry) {
        this.rmiRegistry = rmiRegistry;
    }

    public void init() {
        logger.info("Activated with port {}. Will export services", rmiRegistry.getPort());
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (bean.getClass().isAnnotationPresent(Exportable.class)) {
                Class<?> serviceInterface = bean.getClass().getAnnotation(Exportable.class).value();
                if (!(serviceInterface.isInterface() && serviceInterface.isInstance(bean))) {
                    throw new IllegalArgumentException("Bean does not implement interface in Exportable annotation." +
                            " Bean: " + beanName + " interface: " + serviceInterface);
                } else {
                    String serviceName = namingStrategy.nameService(bean, beanName);
                    rmiRegistry.exportService(bean, serviceName, (Class<Object>) serviceInterface);
                }
            }
        } catch (RemoteException e) {
            logger.warn("Unable to export bean: " + beanName + " over rmi", e);
        }
        return bean;
    }

    public void setNamingStrategy(RmiServiceNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public static interface RmiServiceNamingStrategy {

        String nameService(Object bean, String beanName);

    }

    static class DefaultRmiServiceNamingStrategy implements RmiServiceNamingStrategy {

        @Override
        public String nameService(Object bean, String beanName) {
            String serviceName;
            if (!beanName.contains(".")) {
                serviceName = beanName;
            } else {
                serviceName = beanName.substring(beanName.lastIndexOf(".") + 1);
            }
            // make first char upper case
            return serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);
        }

    }

}
