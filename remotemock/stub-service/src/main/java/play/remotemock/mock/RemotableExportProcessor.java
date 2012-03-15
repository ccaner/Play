package play.remotemock.mock;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.rmi.RemoteException;

public class RemotableExportProcessor implements BeanPostProcessor, ApplicationContextAware {

    ApplicationContext context;

    Integer registryPort = 1299;

    public RemotableExportProcessor(Integer registryPort) {
        this.registryPort = registryPort;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (bean instanceof Remotable) {
                RmiServiceExporter exporter = new RmiServiceExporter();
                exporter.setRegistryPort(registryPort);
                exporter.setService(bean);
                exporter.setServiceName(beanName + "Remote");
                exporter.setServiceInterface(Remotable.class);
                exporter.afterPropertiesSet();
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
