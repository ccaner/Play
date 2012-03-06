package play.remotemock.mock;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import play.remotemock.mock.util.RmiRegistry;
import play.remotemock.mock.util.RmiUtil;

import java.rmi.RemoteException;

public class RemotableExposeProcessor implements BeanPostProcessor, ApplicationContextAware {

    ApplicationContext context;

    RmiRegistry rmiRegistry;

    public RemotableExposeProcessor(RmiRegistry rmiRegistry) {
        this.rmiRegistry = rmiRegistry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (bean instanceof Remotable) {
                rmiRegistry.exportService((Remotable) bean, beanName + "Remote", Remotable.class);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
