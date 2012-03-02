package play.beanhide;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

public class BeanHideProcessor implements BeanPostProcessor {

    @Autowired
    ApplicationContext context;

    String prefix = "stub.";

    public BeanHideProcessor() {
    }

    public BeanHideProcessor(String prefix) {
        this.prefix = prefix;
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

}
