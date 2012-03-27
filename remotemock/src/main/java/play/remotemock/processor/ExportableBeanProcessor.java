package play.remotemock.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import play.remotemock.annotation.Exportable;
import play.remotemock.util.RmiRegistry;

import java.rmi.RemoteException;

public class ExportableBeanProcessor implements BeanPostProcessor, Ordered {

    private RmiRegistry rmiRegistry;
    private int order = 5;
    private ServiceNamingStrategy namingStrategy = new DefaultServiceNamingStrategy();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Exportable exportable = bean.getClass().getAnnotation(Exportable.class);
        try {
            if (exportable != null) {
                Class<?> serviceInterface = exportable.value();
                rmiRegistry.exportService(bean, namingStrategy.getServiceName(bean, beanName, serviceInterface),
                        (Class<Object>) serviceInterface);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public void setRmiRegistry(RmiRegistry rmiRegistry) {
        this.rmiRegistry = rmiRegistry;
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

    static class DefaultServiceNamingStrategy implements ServiceNamingStrategy {

        @Override
        public String getServiceName(Object bean, String beanName, Class<?> serviceInterface) {
            String result;
            if (beanName.contains(".")) {
                result = beanName.substring(beanName.lastIndexOf(".") + 1);
            } else {
                result = beanName;
            }
            return result.substring(0, 1).toUpperCase() + result.substring(1);
        }

    }
}
