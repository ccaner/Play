package play.remotemock.processor;

public interface ServiceNamingStrategy {

    String getServiceName(Object bean, String beanName, Class<?> serviceInterface);

}
