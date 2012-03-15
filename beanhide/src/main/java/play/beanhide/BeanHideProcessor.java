package play.beanhide;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BeanHideProcessor implements BeanPostProcessor, ApplicationContextAware {

    ApplicationContext context;

    String prefix = "stub.";

    public BeanHideProcessor() {
        System.out.println("crating BeanHideProcessor.");
    }

    public BeanHideProcessor(String prefix) {
        this.prefix = prefix;
        System.out.println("crating BeanHideProcessor with prefix: " + prefix);
    }

    public void init() {
        System.out.println("initializing BeanHideProcessor");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        String candidate = prefix + beanName;
        if (context.containsBean(candidate)) {
            return context.getBean(candidate);
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
