package play.beanhide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

/**
 * Replaces beans named <code>beanName</code> with <code>prefixbeanName</code> if one can be found.
 */
public class BeanHideProcessor implements BeanPostProcessor, BeanFactoryPostProcessor, ApplicationContextAware, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(BeanHideProcessor.class);

    private String prefix = "hides.";

    private ApplicationContext applicationContext;

    private int order;

    public BeanHideProcessor() {
    }

    public BeanHideProcessor(String prefix) {
        this.prefix = prefix;
    }

    public void init() {
        logger.info("Activated with prefix : \"{}\"", prefix);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        String candidate = prefix + beanName;
        if (applicationContext.containsBean(candidate)) {
            logger.info("Hiding bean : {} using : {}", beanName, candidate);
            return applicationContext.getBean(candidate);
        }
        return bean;
    }

    /**
     * An attempt to prevent construction of beans that are to be replaced.
     * <p/>
     * This is needed because some beans connect to database during construction (before we can hide them),
     * and this behaviour breaks stub application startup.
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String defName : beanFactory.getBeanDefinitionNames()) {
            try {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(defName);
                String candidate = prefix + defName;
                String beanClassName = beanDefinition.getBeanClassName();
                if (beanClassName != null && beanFactory.containsBeanDefinition(candidate)) {
                    beanDefinition.setBeanClassName("java.lang.Object");
                    beanDefinition.getConstructorArgumentValues().clear();
                    beanDefinition.getPropertyValues().getPropertyValueList().clear();
                }
            } catch (Exception e) {
                logger.warn("Ignoring replace error", e);
            }
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
